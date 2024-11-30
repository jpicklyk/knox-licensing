package net.sfelabs.core.knox.feature.processor.generator

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toClassName
import net.sfelabs.core.knox.feature.processor.model.FeatureMetadata
import net.sfelabs.core.knox.feature.processor.model.PackageName
import net.sfelabs.core.knox.feature.processor.utils.capitalizeWords
import net.sfelabs.core.knox.feature.processor.utils.getClassName

class HiltModuleGenerator(
    private val environment: SymbolProcessorEnvironment
) {
    fun generate(features: List<FeatureMetadata>) {
        generateFeatureImplementations(features)
        generateFeatureModules(features)
        generateMainModule()
    }

    private fun generateFeatureImplementations(features: List<FeatureMetadata>) {
        features.forEach { feature ->
            val implSpec = TypeSpec.classBuilder("${feature.name.capitalizeWords()}Implementation")
                .addAnnotation(ClassName("javax.inject", "Inject"))
                .addSuperinterface(
                    ClassName(PackageName.FEATURE_MODEL.value, "FeatureImplementation")
                        .parameterizedBy(feature.configType?.toClassName() ?: Any::class.asClassName())
                )
                .primaryConstructor(
                    FunSpec.constructorBuilder()
                        .addParameter("getter", getClassName(feature.getter!!))
                        .addParameter("setter", getClassName(feature.setter!!))
                        .build()
                )
                .addProperty(
                    PropertySpec.builder("getter", getClassName(feature.getter!!))
                        .addModifiers(KModifier.OVERRIDE)
                        .initializer("getter")
                        .build()
                )
                .addProperty(
                    PropertySpec.builder("setter", getClassName(feature.setter!!))
                        .addModifiers(KModifier.OVERRIDE)
                        .initializer("setter")
                        .build()
                )
                .build()

            writeImplementationToFile(implSpec, feature)
        }
    }

    private fun generateFeatureModules(features: List<FeatureMetadata>) {
        features.forEach { feature ->
            val moduleSpec = TypeSpec.objectBuilder("${feature.name.capitalizeWords()}Module")
                .addAnnotation(ClassName("dagger", "Module"))
                .addAnnotation(
                    AnnotationSpec.builder(ClassName("dagger.hilt", "InstallIn"))
                        .addMember("value = [%T::class]", ClassName("dagger.hilt.components", "SingletonComponent"))
                        .build()
                )
                .addFunction(createFeatureProvideFunction(feature))
                .build()

            writeModuleToFile(moduleSpec, feature)
        }
    }

    private fun generateMainModule() {
        val moduleSpec = TypeSpec.objectBuilder("GeneratedFeatureModule")
            .addAnnotation(ClassName("dagger", "Module"))
            .addAnnotation(
                AnnotationSpec.builder(ClassName("dagger.hilt", "InstallIn"))
                    .addMember("value = [%T::class]", ClassName("dagger.hilt.components", "SingletonComponent"))
                    .build()
            )
            .addFunction(createRegistryProvideFunction())
            .build()

        writeMainModuleToFile(moduleSpec)
    }

    private fun createFeatureProvideFunction(feature: FeatureMetadata): FunSpec {
        return FunSpec.builder("provide${feature.name.capitalizeWords()}Implementation")
            .addAnnotation(ClassName("dagger", "Provides"))
            .addAnnotation(ClassName("dagger.multibindings", "IntoSet"))
            .addParameter("getter", getClassName(feature.getter!!))
            .addParameter("setter", getClassName(feature.setter!!))
            .returns(
                ClassName(PackageName.FEATURE_MODEL.value, "FeatureImplementation")
                    .parameterizedBy(STAR)
            )
            .addStatement(
                "return %T(getter, setter)",
                ClassName(
                    getPackageName(feature.getter!!),
                    "${feature.name.capitalizeWords()}Implementation"
                )
            )
            .build()
    }

    private fun createRegistryProvideFunction(): FunSpec {
        return FunSpec.builder("provideFeatureRegistry")
            .addAnnotation(ClassName("dagger", "Provides"))
            .addAnnotation(ClassName("javax.inject", "Singleton"))
            .addParameter(
                ParameterSpec.builder(
                    "implementations",
                    Set::class.asClassName().parameterizedBy(
                        ClassName(PackageName.FEATURE_MODEL.value, "FeatureImplementation")
                            .parameterizedBy(STAR)
                    )
                ).addAnnotation(ClassName("org.jetbrains.annotations", "JvmSuppressWildcards")).build()
            )
            .returns(ClassName(PackageName.FEATURE_REGISTRY.value, "FeatureRegistry"))
            .addCode("""
                return %T().apply {
                    implementations.forEach { impl ->
                        register(createRegistration(impl))
                    }
                }
            """.trimIndent(), ClassName(PackageName.FEATURE_REGISTRY.value, "DefaultFeatureRegistry"))
            .build()
    }

    private fun writeImplementationToFile(implSpec: TypeSpec, feature: FeatureMetadata) {
        try {
            // Write the feature implementation to a file
            val packageName = (feature.getter as KSClassDeclaration).containingFile?.packageName?.asString()
                ?.let { "$it.generated" }
                ?: throw IllegalStateException("Could not determine package for ${feature.name}")

            environment.codeGenerator.createNewFile(
                Dependencies(false),
                packageName,
                "${feature.name.capitalizeWords()}Implementation"
            ).use { output ->
                output.writer().use { writer ->
                    FileSpec.builder(packageName, "${feature.name.capitalizeWords()}Implementation")
                        .addType(implSpec)
                        .build()
                        .writeTo(writer)
                }
            }
        } catch (e: FileAlreadyExistsException) {
            environment.logger.warn("Implementation file already exists for ${feature.name}. Skipping generation.")
        }
    }

    private fun writeModuleToFile(moduleSpec: TypeSpec, feature: FeatureMetadata) {
        try {
            // Write the feature module to a file
            environment.codeGenerator.createNewFile(
                Dependencies(false),
                PackageName.FEATURE_HILT.value,
                "${feature.name.capitalizeWords()}Module"
            ).use { output ->
                output.writer().use { writer ->
                    FileSpec.builder(PackageName.FEATURE_HILT.value, "${feature.name.capitalizeWords()}Module")
                        .addType(moduleSpec)
                        .build()
                        .writeTo(writer)
                }
            }
        } catch (e: FileAlreadyExistsException) {
            environment.logger.warn("Module file already exists for ${feature.name}. Skipping generation.")
        }
    }

    private fun writeMainModuleToFile(moduleSpec: TypeSpec) {
        try {
            // Write the main module to a file
            environment.codeGenerator.createNewFile(
                Dependencies(false),
                PackageName.FEATURE_HILT.value,
                "GeneratedFeatureModule"
            ).use { output ->
                output.writer().use { writer ->
                    FileSpec.builder(PackageName.FEATURE_HILT.value, "GeneratedFeatureModule")
                        .addType(moduleSpec)
                        .build()
                        .writeTo(writer)
                }
            }
        } catch (e: FileAlreadyExistsException) {
            environment.logger.warn("Module file already exists. Skipping generation.")
        }
    }

    private fun getPackageName(symbol: KSClassDeclaration): String {
        return symbol.containingFile?.packageName?.asString()
            ?: throw IllegalStateException("Could not determine package for ${symbol.simpleName.asString()}")
    }
}