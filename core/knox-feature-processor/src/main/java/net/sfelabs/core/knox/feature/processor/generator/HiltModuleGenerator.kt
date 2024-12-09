package net.sfelabs.core.knox.feature.processor.generator

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toClassName
import net.sfelabs.core.knox.feature.domain.model.FeatureCategory
import net.sfelabs.core.knox.feature.processor.model.FeatureMetadata
import net.sfelabs.core.knox.feature.processor.model.PackageName
import net.sfelabs.core.knox.feature.processor.utils.capitalizeWords
import net.sfelabs.core.knox.feature.processor.utils.getClassName

class HiltModuleGenerator(
    private val environment: SymbolProcessorEnvironment
) {
    fun generate(features: List<FeatureMetadata>) {
        if (features.isEmpty()) {
            environment.logger.warn("No features found to process")
            return
        }

        generateFeatureModule(features)
        generateUseCaseModule(features)
        generateModuleIndex(features)
    }

    private fun generateFeatureModule(features: List<FeatureMetadata>) {
        val moduleSpec = TypeSpec.classBuilder("GeneratedFeatureModule")
            .addModifiers(KModifier.ABSTRACT)  // Change to abstract class
            .addAnnotation(ClassName("dagger", "Module"))
            .addAnnotation(
                AnnotationSpec.builder(ClassName("dagger.hilt", "InstallIn"))
                    .addMember("value = [%T::class]", ClassName("dagger.hilt.components", "SingletonComponent"))
                    .build()
            )
            .addFunction(
                FunSpec.builder("bindFeatureRegistry")  // Change to bind method
                    .addModifiers(KModifier.ABSTRACT)
                    .addAnnotation(ClassName("dagger", "Binds"))
                    .addAnnotation(ClassName("javax.inject", "Singleton"))
                    .addParameter(
                        "impl",
                        ClassName(PackageName.FEATURE_HILT.value, "HiltFeatureRegistry")
                    )
                    .returns(ClassName(PackageName.FEATURE_REGISTRY.value, "FeatureRegistry"))
                    .build()
            )
            .build()

        writeModuleToFile(moduleSpec, "GeneratedFeatureModule", features)
    }

    private fun generateUseCaseModule(features: List<FeatureMetadata>) {
        val moduleSpec = TypeSpec.objectBuilder("GeneratedUseCaseModule")
            .addAnnotation(ClassName("dagger", "Module"))
            .addAnnotation(
                AnnotationSpec.builder(ClassName("dagger.hilt", "InstallIn"))
                    .addMember("value = [%T::class]", ClassName("dagger.hilt.components", "SingletonComponent"))
                    .build()
            )
            .apply {
                features.forEach { feature ->
                    addFunction(
                        FunSpec.builder("provide${feature.name.capitalizeWords()}Getter")
                            .addAnnotation(ClassName("dagger", "Provides"))
                            .addAnnotation(ClassName("javax.inject", "Singleton"))
                            .addParameter(
                                ParameterSpec.builder("context", ClassName("android.content", "Context"))
                                    .addAnnotation(ClassName("dagger.hilt.android.qualifiers", "ApplicationContext"))
                                    .build()
                            )
                            .returns(getClassName(feature.getter!!))
                            .addCode("return %T(context)", getClassName(feature.getter!!))
                            .build()
                    )
                    addFunction(
                        FunSpec.builder("provide${feature.name.capitalizeWords()}Setter")
                            .addAnnotation(ClassName("dagger", "Provides"))
                            .addAnnotation(ClassName("javax.inject", "Singleton"))
                            .addParameter(
                                ParameterSpec.builder("context", ClassName("android.content", "Context"))
                                    .addAnnotation(ClassName("dagger.hilt.android.qualifiers", "ApplicationContext"))
                                    .build()
                            )
                            .returns(getClassName(feature.setter!!))
                            .addCode("return %T(context)", getClassName(feature.setter!!))
                            .build()
                    )
                }
            }
            .build()

        writeModuleToFile(moduleSpec, "GeneratedUseCaseModule", features)
    }

    private fun generateModuleIndex(features: List<FeatureMetadata>) {
        try {
            val packageName = features.firstOrNull()?.let { feature ->
                (feature.getter as KSClassDeclaration).containingFile?.packageName?.asString()
                    ?.let { "$it.generated" }
            } ?: throw IllegalStateException("No features found to determine package")

            // Include all modules - feature modules + generated modules
            val moduleNames = listOf(
                "GeneratedFeatureModule",
                "GeneratedUseCaseModule"
            ) + features.map { "${it.name.capitalizeWords()}Module" }

            val indexSpec = TypeSpec.objectBuilder("GeneratedModuleIndex")
                .addAnnotation(
                    AnnotationSpec.builder(ClassName("dagger", "Module"))
                        .addMember(
                            "includes = [%L]",
                            moduleNames.joinToString(", ") { "${it}::class" }
                        )
                        .build()
                )
                .addAnnotation(
                    AnnotationSpec.builder(ClassName("dagger.hilt", "InstallIn"))
                        .addMember("value = [%T::class]", ClassName("dagger.hilt.components", "SingletonComponent"))
                        .build()
                )
                .build()

            writeModuleIndexToFile(indexSpec, packageName)
        } catch (e: Exception) {
            environment.logger.error("Failed to generate module index: ${e.message}")
        }
    }

    private fun writeModuleToFile(moduleSpec: TypeSpec, fileName: String, features: List<FeatureMetadata>) {
        try {
            val packageName = features.firstOrNull()?.let { feature ->
                (feature.getter as KSClassDeclaration).containingFile?.packageName?.asString()
                    ?.let { "$it.generated" }
            } ?: throw IllegalStateException("No features found to determine package")

            environment.codeGenerator.createNewFile(
                Dependencies(false),
                packageName,
                fileName
            ).use { output ->
                output.writer().use { writer ->
                    FileSpec.builder(packageName, fileName)
                        .addType(moduleSpec)
                        .build()
                        .writeTo(writer)
                }
            }
        } catch (e: FileAlreadyExistsException) {
            environment.logger.warn("Module file $fileName already exists. Skipping generation.")
        }
    }

    private fun writeModuleIndexToFile(indexSpec: TypeSpec, packageName: String) {
        try {
            environment.codeGenerator.createNewFile(
                Dependencies(false),
                packageName,
                "GeneratedModuleIndex"
            ).use { output ->
                output.writer().use { writer ->
                    FileSpec.builder(packageName, "GeneratedModuleIndex")
                        .addType(indexSpec)
                        .build()
                        .writeTo(writer)
                }
            }
            environment.logger.info("Successfully wrote GeneratedModuleIndex")
        } catch (e: FileAlreadyExistsException) {
            environment.logger.warn("Module index file already exists. Skipping generation.")
        }
    }
}