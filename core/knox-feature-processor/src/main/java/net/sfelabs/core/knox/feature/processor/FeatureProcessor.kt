package net.sfelabs.core.knox.feature.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import net.sfelabs.core.knox.feature.domain.model.FeatureCategory
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
        environment.logger.info("Starting Feature processing")

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

            features[name] = FeatureMetadata(name, description, category)
        }

        // Second pass: find getters
        resolver.getAllFiles().forEach { file ->
            // Get Feature annotation from file first
            val featureAnnotation = file.annotations.firstOrNull {
                it.shortName.asString() == "Feature"
            } ?: return@forEach

            file.declarations
                .filterIsInstance<KSClassDeclaration>()
                .filter { cls ->
                    cls.annotations.any { it.shortName.asString() == "FeatureGetter" }
                }
                .forEach { classDeclaration ->
                    val featureName = featureAnnotation.arguments[0].value as String
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
        }

        // Third pass: find setters
        resolver.getAllFiles().forEach { file ->
            // Get Feature annotation from file first
            val featureAnnotation = file.annotations.firstOrNull {
                it.shortName.asString() == "Feature"
            } ?: return@forEach

            file.declarations
                .filterIsInstance<KSClassDeclaration>()
                .filter { cls ->
                    cls.annotations.any { it.shortName.asString() == "FeatureSetter" }
                }
                .forEach { classDeclaration ->
                    val featureName = featureAnnotation.arguments[0].value as String
                    features[featureName]?.let { metadata ->
                        metadata.setter = classDeclaration
                    }
                }
        }

        // Generate code for complete features
        features.values
            .filter { it.getter != null && it.setter != null && it.configType != null }
            .forEach { metadata ->
                generateFeatureKey(metadata)
                generateFeatureHandler(metadata)
                generateRegistration(metadata)
            }

        features.values
            .filter { it.getter != null && it.setter != null }
            .toList()
            .let { completeFeatures ->
                HiltModuleGenerator(environment).generate(completeFeatures)
            }

        return emptyList()
    }

    private fun generateFeatureKey(metadata: FeatureMetadata) {
        val keySpec = TypeSpec.classBuilder("${metadata.name.capitalizeWords()}Key")
            .addSuperinterface(
                ClassName(PackageName.FEATURE_MODEL.value, "FeatureKey")
                    .parameterizedBy(metadata.configType!!.toClassName())
            )
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter(
                        ParameterSpec.builder("featureName", String::class)
                            .defaultValue("%S", metadata.name)
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

        writeToFile(keySpec, "${metadata.name.capitalizeWords()}Key", metadata.name, metadata)
    }

    private fun generateFeatureHandler(metadata: FeatureMetadata) {
        val handlerSpec = TypeSpec.classBuilder("${metadata.name.capitalizeWords()}Handler")
            .addSuperinterface(
                ClassName(PackageName.FEATURE_HANDLER.value, "FeatureHandler")
                    .parameterizedBy(metadata.configType!!.toClassName())
            )
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter(
                        "getter",
                        getClassName(metadata.getter as KSClassDeclaration)
                    )
                    .addParameter(
                        "setter",
                        getClassName(metadata.setter as KSClassDeclaration)
                    )
                    .build()
            )
            .addProperty(
                PropertySpec.builder("getter", getClassName(metadata.getter as KSClassDeclaration))
                    .initializer("getter")
                    .addModifiers(KModifier.PRIVATE)
                    .build()
            )
            .addProperty(
                PropertySpec.builder("setter", getClassName(metadata.setter as KSClassDeclaration))
                    .initializer("setter")
                    .addModifiers(KModifier.PRIVATE)
                    .build()
            )
            .addFunction(generateGetStateFunction(metadata))
            .addFunction(generateSetStateFunction(metadata))
            .build()

        writeToFile(handlerSpec, "${metadata.name.capitalizeWords()}Handler", metadata.name, metadata)
    }

    private fun generateRegistration(metadata: FeatureMetadata) {
        val packageName = getGeneratedPackage(metadata.getter!!)
        val registrationSpec = TypeSpec.classBuilder("${metadata.name.capitalizeWords()}Registration")
            .addSuperinterface(
                ClassName(PackageName.FEATURE_REGISTRY.value, "FeatureRegistration")
                    .parameterizedBy(metadata.configType!!.toClassName())
            )
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter("getter", getClassName(metadata.getter as KSClassDeclaration))
                    .addParameter("setter", getClassName(metadata.setter as KSClassDeclaration))
                    .build()
            )
            .addProperty(
                PropertySpec.builder("getter", getClassName(metadata.getter as KSClassDeclaration))
                    .initializer("getter")
                    .addModifiers(KModifier.PRIVATE)  // Make the property private
                    .build()
            )
            .addProperty(
                PropertySpec.builder("setter", getClassName(metadata.setter as KSClassDeclaration))
                    .initializer("setter")
                    .addModifiers(KModifier.PRIVATE)  // Make the property private
                    .build()
            )
            .addProperty(
                PropertySpec.builder(
                    "key",
                    ClassName(PackageName.FEATURE_MODEL.value, "FeatureKey")
                        .parameterizedBy(metadata.configType!!.toClassName())
                )
                    .initializer("%T()",
                        ClassName(packageName, "${metadata.name.capitalizeWords()}Key")
                    )
                    .addModifiers(KModifier.OVERRIDE)
                    .build()
            )
            .addProperty(
                PropertySpec.builder(
                    "handler",
                    ClassName(PackageName.FEATURE_HANDLER.value, "FeatureHandler")
                        .parameterizedBy(metadata.configType!!.toClassName())
                )
                    .initializer("%T(getter, setter)",
                        ClassName(packageName, "${metadata.name.capitalizeWords()}Handler")
                    )
                    .addModifiers(KModifier.OVERRIDE)
                    .build()
            )
            .addProperty(
                PropertySpec.builder("category", FeatureCategory::class)
                    .initializer("%T.%L", FeatureCategory::class, metadata.category.name)
                    .addModifiers(KModifier.OVERRIDE)
                    .build()
            )
            .addProperty(
                PropertySpec.builder("description", String::class)
                    .initializer("%S", metadata.description)
                    .addModifiers(KModifier.OVERRIDE)
                    .build()
            )
            .build()

        writeToFile(registrationSpec, "${metadata.name.capitalizeWords()}Registration", metadata.name, metadata)
    }

    private fun generateGetStateFunction(metadata: FeatureMetadata): FunSpec {
        return FunSpec.builder("getState")
            .addModifiers(KModifier.OVERRIDE, KModifier.SUSPEND)
            .returns(
                ClassName(PackageName.API_DOMAIN.value, "ApiResult")
                    .parameterizedBy(
                        ClassName(PackageName.FEATURE_MODEL.value, "FeatureState")
                            .parameterizedBy(metadata.configType!!.toClassName())
                    )
            )
            .addCode("return getter(Unit).wrapInFeatureState()")
            .build()
    }

    private fun generateSetStateFunction(metadata: FeatureMetadata): FunSpec {
        return FunSpec.builder("setState")
            .addModifiers(KModifier.OVERRIDE, KModifier.SUSPEND)
            .addParameter(
                "newState",
                ClassName(PackageName.FEATURE_MODEL.value, "FeatureState")
                    .parameterizedBy(metadata.configType!!.toClassName())
            )
            .returns(
                ClassName(PackageName.API_DOMAIN.value, "ApiResult")
                    .parameterizedBy(ClassName("kotlin", "Unit"))
            )
            .addCode("return setter(newState.value)")
            .build()
    }

    private fun getGeneratedPackage(classDeclaration: KSClassDeclaration): String {
        val packageName = classDeclaration.containingFile?.packageName?.asString()
            ?: throw IllegalStateException("Could not determine package name for ${classDeclaration.simpleName.asString()}")
        return "$packageName.generated"
    }

    private fun writeToFile(typeSpec: TypeSpec, fileName: String, featureName: String, metadata: FeatureMetadata) {
        val packageName = getGeneratedPackage(metadata.getter!!)
        try {
            environment.codeGenerator.createNewFile(
                Dependencies(false),
                packageName,
                fileName
            ).use { output ->
                output.writer().use { writer ->
                    val fileSpecBuilder = FileSpec.builder(
                        packageName,
                        fileName
                    ).addType(typeSpec)

                    // Add imports based on file type
                    when {
                        fileName.endsWith("Key") -> {
                            fileSpecBuilder.addImport(PackageName.FEATURE_MODEL.value, "FeatureKey")
                        }
                        fileName.endsWith("Handler") -> {
                            fileSpecBuilder.addImport(PackageName.API_DOMAIN.value, "ApiResult")
                                .addImport(PackageName.FEATURE_MODEL.value, "FeatureState")
                                .addImport(PackageName.FEATURE_HANDLER.value, "FeatureHandler")
                                .addImport(PackageName.FEATURE_MODEL.value, "wrapInFeatureState")
                        }
                        fileName.endsWith("Registration") -> {
                            fileSpecBuilder.addImport(PackageName.FEATURE_REGISTRY.value, "FeatureRegistration")
                                .addImport(PackageName.FEATURE_MODEL.value, "FeatureKey")
                                .addImport(PackageName.FEATURE_HANDLER.value, "FeatureHandler")
                                .addImport(PackageName.FEATURE_MODEL.value, "FeatureCategory")
                        }
                    }

                    fileSpecBuilder.build().writeTo(writer)
                }
            }
        } catch (e: FileAlreadyExistsException) {
            environment.logger.warn("File $fileName already exists for feature $featureName. Skipping generation.")
        }
    }
}