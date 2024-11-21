package net.sfelabs.core.knox.feature.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.feature.annotation.FeatureUseCase
import net.sfelabs.core.knox.feature.domain.FeatureRegistry
import net.sfelabs.core.knox.feature.domain.model.*
import java.io.File
import java.util.Locale

enum class PackageName(val value: String) {
    FEATURE_DOMAIN("net.sfelabs.core.knox.feature.domain"),
    FEATURE_MODEL("net.sfelabs.core.knox.feature.domain.model"),
    FEATURE_GENERATED("net.sfelabs.core.knox.feature.generated"),
    API("net.sfelabs.core.knox.api.domain")
}

class FeatureProcessor(
    private val environment: SymbolProcessorEnvironment
) : SymbolProcessor {
    private val features = mutableMapOf<String, FeatureGroup>()

    data class FeatureGroup(
        val featureName: String,
        val category: FeatureCategory,
        val configType: KSType,
        var getter: KSAnnotated? = null,
        var setter: KSAnnotated? = null
    )

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(FeatureUseCase::class.qualifiedName!!)

        symbols.forEach { symbol ->
            val annotation = symbol.annotations.first {
                it.shortName.asString() == "FeatureUseCase"
            }

            val featureName = annotation.arguments[0].value as String
            val type = (annotation.arguments.find { it.name?.asString() == "type" }?.value as KSType)
                .declaration.simpleName.asString().let { enumValue ->
                    FeatureUseCase.Type.valueOf(enumValue)
                }

            val category = (annotation.arguments.find { it.name?.asString() == "category" }?.value as KSType)
                .declaration.simpleName.asString().let { enumValue ->
                    FeatureCategory.valueOf(enumValue)
                }

            val config = annotation.arguments.find { it.name?.asString() == "config" }?.value as KSType

            features.getOrPut(featureName) {
                FeatureGroup(featureName, category, config)
            }.apply {
                when (type) {
                    FeatureUseCase.Type.GETTER -> getter = symbol
                    FeatureUseCase.Type.SETTER -> setter = symbol
                }
            }
        }

        features.values
            .filter { it.getter != null && it.setter != null }
            .forEach { feature ->
                generateFeatureKey(feature)
                generateFeatureHandler(feature)
            }

        updateFeatureRegistry(features.values.toList())

        return emptyList()
    }

    private fun getClassName(declaration: KSClassDeclaration): ClassName {
        val qualifiedName = declaration.qualifiedName?.asString()
            ?: throw IllegalStateException("Could not resolve class name")
        return ClassName.bestGuess(qualifiedName)
    }

    private fun KSType.toClassName(): ClassName {
        return when (val declaration = this.declaration) {
            is KSClassDeclaration -> getClassName(declaration)
            else -> throw IllegalStateException("Type declaration is not a class declaration")
        }
    }

    private fun generateFeatureKey(feature: FeatureGroup) {
        val keySpec = TypeSpec.classBuilder("${feature.featureName.capitalizeWords()}Key")
            .addSuperinterface(
                ClassName(PackageName.FEATURE_MODEL.value, "FeatureKey")
                    .parameterizedBy(feature.configType.toClassName())
            )
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter(
                        ParameterSpec.builder("featureName", String::class)
                            .defaultValue("%S", feature.featureName)
                            .build()
                    )
                    .build()
            )
            .addProperty(
                PropertySpec.builder("featureName", String::class)
                    .initializer("featureName")
                    .addModifiers(KModifier.OVERRIDE)
                    .build()
            )
            .build()

        try {
            environment.codeGenerator.createNewFile(
                Dependencies(false),
                PackageName.FEATURE_GENERATED.value,
                "${feature.featureName.capitalizeWords()}Key"
            ).use { output ->
                output.writer().use { writer ->
                    FileSpec.builder(
                        PackageName.FEATURE_GENERATED.value,
                        "${feature.featureName.capitalizeWords()}Key"
                    )
                        .addImport(PackageName.FEATURE_MODEL.value, "FeatureKey")
                        .addType(keySpec)
                        .build()
                        .writeTo(writer)
                }
            }
        } catch (e: FileAlreadyExistsException) {
            // Handle the case where the file already exists
            environment.logger.warn(
                "File ${feature.featureName.capitalizeWords()}" +
                        "Key.kt already exists. Skipping generation."
            )
        }
    }

    private fun generateFeatureHandler(feature: FeatureGroup) {
        val handlerSpec = TypeSpec.classBuilder("${feature.featureName.capitalizeWords()}Handler")
            .addSuperinterface(
                ClassName(PackageName.FEATURE_MODEL.value, "FeatureHandler")
                    .parameterizedBy(feature.configType.toClassName())
            )
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter(
                        "getter",
                        getClassName(feature.getter as KSClassDeclaration)
                    )
                    .addParameter(
                        "setter",
                        getClassName(feature.setter as KSClassDeclaration)
                    )
                    .build()
            )
            .addProperty(
                PropertySpec.builder("getter", getClassName(feature.getter as KSClassDeclaration))
                    .initializer("getter")
                    .addModifiers(KModifier.PRIVATE)
                    .build()
            )
            .addProperty(
                PropertySpec.builder("setter", getClassName(feature.setter as KSClassDeclaration))
                    .initializer("setter")
                    .addModifiers(KModifier.PRIVATE)
                    .build()
            )
            .addFunction(
                FunSpec.builder("getState")
                    .addModifiers(KModifier.OVERRIDE, KModifier.SUSPEND)
                    .returns(
                        ClassName(PackageName.API.value, "ApiResult")
                            .parameterizedBy(
                                ClassName(PackageName.FEATURE_MODEL.value, "FeatureState")
                                    .parameterizedBy(feature.configType.toClassName())
                            )
                    )
                    .addCode("return getter(Unit).wrapInFeatureState()")
                    .build()
            )
            .addFunction(
                FunSpec.builder("setState")
                    .addModifiers(KModifier.OVERRIDE, KModifier.SUSPEND)
                    .addParameter(
                        "newState",
                        ClassName(PackageName.FEATURE_MODEL.value, "FeatureState")
                            .parameterizedBy(feature.configType.toClassName())
                    )
                    .returns(
                        ClassName(PackageName.API.value, "ApiResult")
                            .parameterizedBy(ClassName("kotlin", "Unit"))
                    )
                    .addCode("return setter(newState.value)")
                    .build()
            )
            .build()

        try {
            environment.codeGenerator.createNewFile(
                Dependencies(false),
                PackageName.FEATURE_GENERATED.value,
                "${feature.featureName.capitalizeWords()}Handler"
            ).use { output ->
                output.writer().use { writer ->
                    FileSpec.builder(
                        PackageName.FEATURE_GENERATED.value,
                        "${feature.featureName.capitalizeWords()}Handler"
                    )
                        .addImport(PackageName.API.value, "ApiResult")
                        .addImport(PackageName.FEATURE_MODEL.value, "FeatureState")
                        .addImport(PackageName.FEATURE_MODEL.value, "FeatureHandler")
                        .addImport(PackageName.FEATURE_MODEL.value, "wrapInFeatureState")
                        .addType(handlerSpec)
                        .build()
                        .writeTo(writer)
                }
            }
        } catch (e: FileAlreadyExistsException) {
            // Handle the case where the file already exists
            environment.logger.warn(
                "File ${feature.featureName.capitalizeWords()}" +
                        "Handler.kt already exists. Skipping generation."
            )
        }
    }

    private fun updateFeatureRegistry(features: List<FeatureGroup>) {
        val registrySpec = TypeSpec.classBuilder("GeneratedFeatureRegistry")
            .addSuperinterface(FeatureRegistry::class)
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter(
                        ParameterSpec.builder(
                            "handlers",
                            Map::class.asClassName().parameterizedBy(
                                String::class.asClassName(),
                                FeatureHandler::class.asClassName().parameterizedBy(STAR)
                            )
                        ).build()
                    )
                    .addParameter(
                        ParameterSpec.builder(
                            "categories",
                            Map::class.asClassName().parameterizedBy(
                                String::class.asClassName(),
                                FeatureCategory::class.asClassName()
                            )
                        ).build()
                    )
                    .build()
            )
            .addProperty(
                PropertySpec.builder("handlers",
                    Map::class.asClassName().parameterizedBy(
                        String::class.asClassName(),
                        FeatureHandler::class.asClassName().parameterizedBy(STAR)
                    )
                )
                    .initializer("handlers")
                    .addModifiers(KModifier.PRIVATE)
                    .build()
            )
            .addProperty(
                PropertySpec.builder("categories",
                    Map::class.asClassName().parameterizedBy(
                        String::class.asClassName(),
                        FeatureCategory::class.asClassName()
                    )
                )
                    .initializer("categories")
                    .addModifiers(KModifier.PRIVATE)
                    .build()
            )
            .addFunction(
                FunSpec.builder("getHandler")
                    .addModifiers(KModifier.OVERRIDE)
                    .addTypeVariable(TypeVariableName("T", ANY))
                    .addParameter("key", FeatureKey::class.asClassName().parameterizedBy(TypeVariableName("T")))
                    .returns(FeatureHandler::class.asClassName().parameterizedBy(TypeVariableName("T")).copy(nullable = true))
                    .addCode("return handlers[key.featureName] as? FeatureHandler<T>")
                    .build()
            )
            .addFunction(
                FunSpec.builder("getFeatures")
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter("category", FeatureCategory::class)
                    .returns(List::class.asClassName().parameterizedBy(Feature::class.asClassName().parameterizedBy(STAR)))
                    .addCode("""
                       return categories.filter { it.value == category }
                           .map { (name, _) ->
                               when (val stateResult = runBlocking { (handlers[name] as FeatureHandler<*>).getState() }) {
                                   is ApiResult.Success -> Feature(
                                       key = findKeyByName(name),
                                       state = stateResult.data
                                   )
                                   else -> throw IllegalStateException("Failed to get feature state")
                               }
                           }
                   """.trimIndent())
                    .build()
            )
            .addFunction(
                FunSpec.builder("isRegistered")
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter(
                        "key",
                        ClassName(PackageName.FEATURE_MODEL.value, "FeatureKey")
                            .parameterizedBy(STAR)
                    )
                    .returns(Boolean::class)
                    .addCode("return handlers.containsKey(key.featureName)")
                    .build()
            )
            .addFunction(createFindKeyByNameFunction(features))
            .addType(createCompanionObject(features))
            .build()

        try {
            environment.codeGenerator.createNewFile(
                Dependencies(false),
                PackageName.FEATURE_GENERATED.value,
                "GeneratedFeatureRegistry"
            ).use { output ->
                output.writer().use { writer ->
                    FileSpec.builder(PackageName.FEATURE_GENERATED.value, "GeneratedFeatureRegistry")
                        .addImport(PackageName.FEATURE_DOMAIN.value, "FeatureRegistry")
                        .addImport(PackageName.FEATURE_MODEL.value, "FeatureHandler")
                        .addImport(PackageName.FEATURE_MODEL.value, "FeatureKey")
                        .addImport(PackageName.FEATURE_MODEL.value, "Feature")
                        .addImport(PackageName.FEATURE_MODEL.value, "FeatureCategory")
                        .addImport(PackageName.API.value, "ApiResult")
                        .addImport("kotlinx.coroutines", "runBlocking")
                        .addType(registrySpec)
                        .build()
                        .writeTo(writer)
                }
            }
        } catch (e: FileAlreadyExistsException) {
            // Handle the case where the file already exists
            environment.logger.warn(
                "File GeneratedFeatureRegistry.kt already exists. Skipping generation."
            )
        }
    }

    private fun createFindKeyByNameFunction(features: List<FeatureGroup>): FunSpec {
        return FunSpec.builder("findKeyByName")
            .addModifiers(KModifier.PRIVATE)
            .addParameter("name", String::class)
            .returns(ClassName(PackageName.FEATURE_MODEL.value, "FeatureKey").parameterizedBy(ANY))  // Change STAR to ANY
            .addCode(
                CodeBlock.builder()
                    .add("return when (name) {\n")
                    .apply {
                        features.forEach { feature ->
                            add("    %S -> %T(name)\n",
                                feature.featureName,
                                ClassName(
                                    PackageName.FEATURE_GENERATED.value,
                                    "${feature.featureName.capitalizeWords()}Key"
                                )
                            )
                        }
                    }
                    .add("    else -> throw IllegalArgumentException(\"Unknown feature: \$name\")\n")
                    .add("}")
                    .build()
            )
            .build()
    }

    private fun createCompanionObject(features: List<FeatureGroup>): TypeSpec {
        return TypeSpec.companionObjectBuilder()
            .addFunction(
                FunSpec.builder("create")
                    .apply {
                        // Add parameters for each feature's getter and setter
                        features.forEach { feature ->
                            val className = feature.featureName.capitalizeWords()
                            addParameter(
                                "${feature.featureName}Getter",
                                getClassName(feature.getter as KSClassDeclaration)
                            )
                            addParameter(
                                "${feature.featureName}Setter",
                                getClassName(feature.setter as KSClassDeclaration)
                            )
                        }
                    }
                    .returns(FeatureRegistry::class.asClassName())
                    .addCode(buildFactoryMethodBody(features))
                    .build()
            )
            .build()
    }

    private fun buildFactoryMethodBody(features: List<FeatureGroup>): CodeBlock {
        return CodeBlock.builder()
            .addStatement("val handlers: Map<String, FeatureHandler<*>> = mapOf(")
            .apply {
                features.forEachIndexed { index, feature ->
                    add("    %S to %T(getter = %N, setter = %N)%L\n",
                        feature.featureName,
                        ClassName(
                            PackageName.FEATURE_GENERATED.value,
                            "${feature.featureName.capitalizeWords()}Handler"
                        ),
                        "${feature.featureName}Getter",
                        "${feature.featureName}Setter",
                        if (index < features.size - 1) "," else ""
                    )
                }
            }
            .addStatement(")")
            .addStatement("val categories: Map<String, FeatureCategory> = mapOf(")
            .apply {
                features.forEachIndexed { index, feature ->
                    add("    %S to %T.%L%L\n",
                        feature.featureName,
                        FeatureCategory::class,
                        feature.category.name,
                        if (index < features.size - 1) "," else ""
                    )
                }
            }
            .addStatement(")")
            .addStatement("return %T(handlers, categories)",
                ClassName(PackageName.FEATURE_GENERATED.value, "GeneratedFeatureRegistry")
            )
            .build()
    }
}

fun String.capitalizeWords(): String {
    return split("_")
        .map { it.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } }
        .joinToString("")
}