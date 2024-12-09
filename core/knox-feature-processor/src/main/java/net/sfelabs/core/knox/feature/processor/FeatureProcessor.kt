package net.sfelabs.core.knox.feature.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import net.sfelabs.core.knox.feature.domain.model.FeatureCategory
import net.sfelabs.core.knox.feature.domain.model.StateMapping
import net.sfelabs.core.knox.feature.processor.generator.HiltModuleGenerator
import net.sfelabs.core.knox.feature.processor.model.FeatureMetadata
import net.sfelabs.core.knox.feature.processor.model.PackageName
import net.sfelabs.core.knox.feature.processor.utils.*
import net.sfelabs.core.knox.feature.processor.utils.capitalizeWords

class FeatureProcessor(
    val environment: SymbolProcessorEnvironment
) : SymbolProcessor {
    private val features = mutableMapOf<String, FeatureMetadata>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        // First pass: collect files with Feature annotation
        resolver.getAllFiles().forEach { file ->
            val featureAnnotation = file.annotations.firstOrNull {
                it.shortName.asString() == "Feature"
            } ?: return@forEach

            val name = featureAnnotation.arguments.find {
                it.name?.asString() == "name"
            }?.value as? String ?: return@forEach

            val description = featureAnnotation.arguments.find {
                it.name?.asString() == "description"
            }?.value as? String ?: return@forEach

            val categoryType = featureAnnotation.arguments.find {
                it.name?.asString() == "category"
            }?.value as? KSType ?: return@forEach

            val category = categoryType.declaration.simpleName.asString().let { enumValue ->
                FeatureCategory.valueOf(enumValue)
            }

            val stateMapping = featureAnnotation.arguments.find {
                it.name?.asString() == "stateMapping"
            }?.value?.let { value ->
                val enumValue = (value as KSType).declaration.simpleName.asString()
                StateMapping.valueOf(enumValue)
            } ?: StateMapping.DIRECT

            features[name] = FeatureMetadata(name, description, category, stateMapping)
        }

        // Second pass: find getters and setters
        resolver.getAllFiles().forEach { file ->
            processGettersAndSetters(file)
        }

        val completeFeatures = features.values.filter {
            it.getter != null && it.setter != null && it.configType != null
        }

        if (completeFeatures.isNotEmpty()) {
            generateComponents(completeFeatures)
            generateHiltModule(completeFeatures)
        }

        return emptyList()
    }

    private fun generateHiltModule(features: List<FeatureMetadata>) {
        HiltModuleGenerator(environment).generate(features)
    }

    private fun processGettersAndSetters(file: KSFile) {
        val featureAnnotation = file.annotations.firstOrNull {
            it.shortName.asString() == "Feature"
        } ?: return

        val featureName = featureAnnotation.arguments.find {
            it.name?.asString() == "name"
        }?.value as? String ?: return

        // Process getters
        file.declarations
            .filterIsInstance<KSClassDeclaration>()
            .filter { cls ->
                cls.annotations.any { it.shortName.asString() == "FeatureGetter" }
            }
            .forEach { classDeclaration ->
                environment.logger.info("Found getter: ${classDeclaration.simpleName.asString()}")
                features[featureName]?.let { metadata ->
                    metadata.getter = classDeclaration
                    metadata.configType = classDeclaration.superTypes
                        .first()
                        .resolve()
                        .arguments
                        .last()
                        .type
                        ?.resolve()
                }
            }

        // Process setters
        file.declarations
            .filterIsInstance<KSClassDeclaration>()
            .filter { cls ->
                cls.annotations.any { it.shortName.asString() == "FeatureSetter" }
            }
            .forEach { classDeclaration ->
                environment.logger.info("Found setter: ${classDeclaration.simpleName.asString()}")
                features[featureName]?.let { metadata ->
                    metadata.setter = classDeclaration
                }
            }
    }

    private fun generateComponents(features: List<FeatureMetadata>) {
        features.forEach { metadata ->
            // Generate Key first
            generateKey(metadata)

            // Generate Component
            val componentSpec = TypeSpec.classBuilder("${metadata.name.capitalizeWords()}Component")
                .primaryConstructor(
                    FunSpec.constructorBuilder()
                        .addAnnotation(ClassName("javax.inject", "Inject"))
                        .addParameter(
                            "getter",
                            getClassName(metadata.getter!!)
                        )
                        .addParameter(
                            "setter",
                            getClassName(metadata.setter!!)
                        )
                        .addCode("""
                            |println("Creating component: ${metadata.name}")
                            |""".trimMargin())
                        .build()
                )
                .addSuperinterface(
                    ClassName(PackageName.FEATURE_MODEL.value, "FeatureComponent")
                        .parameterizedBy(metadata.configType!!.toClassName())
                )
                .addInitializerBlock(
                    CodeBlock.builder()
                        .addStatement("println(%S)", "Initializing component: \${featureName}")
                        .build()
                )
                .addProperty(
                    PropertySpec.builder("getter", getClassName(metadata.getter!!))
                        .initializer("getter")
                        .addModifiers(KModifier.PRIVATE)
                        .build()
                )
                .addProperty(
                    PropertySpec.builder("setter", getClassName(metadata.setter!!))
                        .initializer("setter")
                        .addModifiers(KModifier.PRIVATE)
                        .build()
                )
                .addProperty(
                    PropertySpec.builder("featureName", String::class)
                        .addModifiers(KModifier.OVERRIDE)
                        .initializer("%S", metadata.name)
                        .build()
                )
                .addProperty(
                    PropertySpec.builder("description", String::class)
                        .addModifiers(KModifier.OVERRIDE)
                        .initializer("%S", metadata.description)
                        .build()
                )
                .addProperty(
                    PropertySpec.builder("category", FeatureCategory::class)
                        .addModifiers(KModifier.OVERRIDE)
                        .initializer("%T.%L", FeatureCategory::class, metadata.category.name)
                        .build()
                )
                .addProperty(
                    PropertySpec.builder("handler",
                        ClassName(PackageName.FEATURE_HANDLER.value, "FeatureHandler")
                            .parameterizedBy(metadata.configType!!.toClassName())
                    )
                        .addModifiers(KModifier.OVERRIDE)
                        .initializer(
                            "DefaultFeatureHandler(getter, setter, %T.%L)",
                            ClassName(PackageName.FEATURE_MODEL.value, "StateMapping"),
                            metadata.stateMapping.name
                        )
                        .build()
                )
                .addProperty(
                    PropertySpec.builder("defaultValue", metadata.configType!!.toClassName())
                        .addModifiers(KModifier.OVERRIDE)
                        .initializer(getDefaultValueForType(metadata.configType!!))
                        .build()
                )
                .addProperty(
                    PropertySpec.builder("key",
                        ClassName(PackageName.FEATURE_MODEL.value, "FeatureKey")
                            .parameterizedBy(metadata.configType!!.toClassName())
                    )
                        .addModifiers(KModifier.OVERRIDE)
                        .initializer(
                            "%T",
                            ClassName(getGeneratedPackage(metadata.getter!!), "${metadata.name.capitalizeWords()}Key")
                        )
                        .build()
                )
                .build()

            writeComponentToFile(componentSpec, metadata)
            generateComponentModule(metadata)
        }
    }

    private fun generateComponentModule(metadata: FeatureMetadata) {
        val moduleSpec = TypeSpec.classBuilder("${metadata.name.capitalizeWords()}Module")
            .addModifiers(KModifier.ABSTRACT)
            .addAnnotation(ClassName("dagger", "Module"))
            .addAnnotation(
                AnnotationSpec.builder(ClassName("dagger.hilt", "InstallIn"))
                    .addMember("%T::class", ClassName("dagger.hilt.components", "SingletonComponent"))
                    .build()
            )
            .addFunction(
                FunSpec.builder("bind")
                    .addModifiers(KModifier.ABSTRACT)
                    .addAnnotation(ClassName("dagger", "Binds"))
                    .addAnnotation(ClassName("dagger.multibindings", "IntoSet"))
                    .addParameter(
                        "impl",
                        ClassName(
                            getGeneratedPackage(metadata.getter!!),
                            "${metadata.name.capitalizeWords()}Component"
                        )
                    )
                    .returns(
                        ClassName(PackageName.FEATURE_MODEL.value, "FeatureComponent")
                            .parameterizedBy(WildcardTypeName.producerOf(ANY))
                    )
                    .build()
            )
            .addInitializerBlock(
                CodeBlock.builder()
                    .addStatement("println(%S)", "Initializing ${metadata.name.capitalizeWords()}Module")
                    .build()
            )
            .build()

        writeModuleToFile(moduleSpec, metadata)
    }

    private fun generateKey(metadata: FeatureMetadata) {
        val keySpec = TypeSpec.objectBuilder("${metadata.name.capitalizeWords()}Key")
            .addSuperinterface(
                ClassName(PackageName.FEATURE_MODEL.value, "FeatureKey")
                    .parameterizedBy(metadata.configType!!.toClassName())
            )
            .addProperty(
                PropertySpec.builder("featureName", String::class)
                    .addModifiers(KModifier.OVERRIDE)
                    .initializer("%S", metadata.name)
                    .build()
            )
            .build()

        writeKeyToFile(keySpec, metadata)
    }

    private fun writeComponentToFile(componentSpec: TypeSpec, metadata: FeatureMetadata) {
        try {
            val packageName = getGeneratedPackage(metadata.getter!!)

            environment.codeGenerator.createNewFile(
                Dependencies(false),
                packageName,
                "${metadata.name.capitalizeWords()}Component"
            ).use { output ->
                output.writer().use { writer ->
                    FileSpec.builder(packageName, "${metadata.name.capitalizeWords()}Component")
                        .addType(componentSpec)
                        .addImport(PackageName.FEATURE_MODEL.value, "FeatureComponent")
                        .addImport(PackageName.FEATURE_HANDLER.value, "FeatureHandler")
                        .addImport(PackageName.FEATURE_HANDLER.value, "DefaultFeatureHandler")
                        .addImport(PackageName.FEATURE_MODEL.value, "FeatureCategory")
                        .addImport(packageName, "${metadata.name.capitalizeWords()}Key")
                        .build()
                        .writeTo(writer)
                }
            }
        } catch (e: FileAlreadyExistsException) {
            environment.logger.warn("Component file already exists for ${metadata.name}. Skipping generation.")
        }
    }

    private fun writeModuleToFile(moduleSpec: TypeSpec, metadata: FeatureMetadata) {
        try {
            val packageName = getGeneratedPackage(metadata.getter!!)

            environment.codeGenerator.createNewFile(
                Dependencies(false),
                packageName,
                "${metadata.name.capitalizeWords()}Module"
            ).use { output ->
                output.writer().use { writer ->
                    FileSpec.builder(packageName, "${metadata.name.capitalizeWords()}Module")
                        .addType(moduleSpec)
                        .build()
                        .writeTo(writer)
                }
            }
        } catch (e: FileAlreadyExistsException) {
            environment.logger.warn("Module file already exists for ${metadata.name}. Skipping generation.")
        }
    }

    private fun writeKeyToFile(keySpec: TypeSpec, metadata: FeatureMetadata) {
        try {
            val packageName = getGeneratedPackage(metadata.getter!!)

            environment.codeGenerator.createNewFile(
                Dependencies(false),
                packageName,
                "${metadata.name.capitalizeWords()}Key"
            ).use { output ->
                output.writer().use { writer ->
                    FileSpec.builder(packageName, "${metadata.name.capitalizeWords()}Key")
                        .addType(keySpec)
                        .build()
                        .writeTo(writer)
                }
            }
        } catch (e: FileAlreadyExistsException) {
            environment.logger.warn("Key file already exists for ${metadata.name}. Skipping generation.")
        }
    }

    private fun getDefaultValueForType(type: KSType): String {
        return when (type.declaration.simpleName.asString()) {
            "Boolean" -> "false"
            "Int" -> "0"
            "Long" -> "0L"
            "Float" -> "0f"
            "Double" -> "0.0"
            "String" -> "\"\""
            else -> "null"
        }
    }

    private fun getGeneratedPackage(classDeclaration: KSClassDeclaration): String {
        return classDeclaration.containingFile?.packageName?.asString()?.let { "$it.generated" }
            ?: throw IllegalStateException("Could not determine package name")
    }
}