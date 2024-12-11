package net.sfelabs.core.knox.feature.processor.generator

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.WildcardTypeName
import net.sfelabs.core.knox.feature.processor.model.PackageName
import net.sfelabs.core.knox.feature.processor.model.ProcessedFeature
import net.sfelabs.core.knox.feature.processor.utils.GeneratedPackages

class ModuleGenerator(
    private val environment: SymbolProcessorEnvironment
) {
    fun generate(features: List<ProcessedFeature>) {
        if (features.isEmpty()) return

        // Generate individual feature modules first
        features.forEach { feature ->
            generateFeatureModule(feature)
        }
        // Generate the FeatureRegistry binding
        generateFeatureRegistryModule()
        // Finally generate the index
        generateModuleIndex(features)
    }

    private fun generateFeatureModule(feature: ProcessedFeature) {
        val moduleSpec = TypeSpec.classBuilder("${feature.className}Module")
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
                        ClassName(getGeneratedPackage(), "${feature.className}Component")
                    )
                    .returns(
                        ClassName(PackageName.FEATURE_COMPONENT.value, "FeatureComponent")
                            .parameterizedBy(WildcardTypeName.producerOf(ANY))
                    )
                    .build()
            )
            .build()

        writeModuleToFile(moduleSpec, feature.className)
    }

    private fun generateModuleIndex(features: List<ProcessedFeature>) {
        if (features.isEmpty()) return

        val packageName = getGeneratedPackage()
        val moduleNames = features.map { "${it.className}Module" } + "FeatureRegistryModule"

        val indexSpec = TypeSpec.objectBuilder("GeneratedModuleIndex")
            .addAnnotation(
                AnnotationSpec.builder(ClassName("dagger", "Module"))
                    .addMember(
                        moduleNames.joinToString(
                            prefix = "includes = [",
                            postfix = "]"
                        ) { "%T::class" },
                        *moduleNames.map {
                            ClassName(packageName, it)
                        }.toTypedArray()
                    )
                    .build()
            )
            .addAnnotation(
                AnnotationSpec.builder(ClassName("dagger.hilt", "InstallIn"))
                    .addMember("%T::class", ClassName("dagger.hilt.components", "SingletonComponent"))
                    .build()
            )
            .build()

        writeModuleIndexToFile(indexSpec)
    }

    private fun generateFeatureRegistryModule() {
        val moduleSpec = TypeSpec.classBuilder("FeatureRegistryModule")
            .addModifiers(KModifier.ABSTRACT)
            .addAnnotation(ClassName("dagger", "Module"))
            .addAnnotation(
                AnnotationSpec.builder(ClassName("dagger.hilt", "InstallIn"))
                    .addMember("%T::class", ClassName("dagger.hilt.components", "SingletonComponent"))
                    .build()
            )
            .addFunction(
                FunSpec.builder("bindFeatureRegistry")
                    .addModifiers(KModifier.ABSTRACT)
                    .addAnnotation(ClassName("dagger", "Binds"))
                    .addAnnotation(ClassName("javax.inject", "Singleton"))
                    .addParameter(
                        "impl",
                        ClassName(PackageName.FEATURE_HILT.value, "HiltFeatureRegistry")
                    )
                    .returns(
                        ClassName(PackageName.FEATURE_REGISTRY.value, "FeatureRegistry")
                    )
                    .build()
            )
            .build()

        writeRegistryModuleToFile(moduleSpec)
    }

    private fun writeModuleToFile(
        moduleSpec: TypeSpec,
        fileName: String
    ) {
        val moduleName = fileName + "Module"
        try {
            val packageName = getGeneratedPackage()

            environment.codeGenerator.createNewFile(
                Dependencies(false),
                packageName,
                moduleName
            ).use { output ->
                output.writer().use { writer ->
                    FileSpec.builder(packageName, moduleName)
                        .addType(moduleSpec)
                        .addImport(PackageName.FEATURE_COMPONENT.value, "FeatureComponent")
                        .addImport(
                            getFeaturePackage(),
                            fileName + "Component"
                        )
                        .build()
                        .writeTo(writer)
                }
            }
        } catch (_: FileAlreadyExistsException) {
            environment.logger.warn("Module file $moduleName already exists. Skipping generation.")
        }
    }

    private fun writeRegistryModuleToFile(
        moduleSpec: TypeSpec,
        fileName: String = "FeatureRegistryModule"
    ) {
        try {
            val packageName = getGeneratedPackage()

            environment.codeGenerator.createNewFile(
                Dependencies(false),
                packageName,
                fileName
            ).use { output ->
                output.writer().use { writer ->
                    FileSpec.builder(packageName, fileName)
                        .addType(moduleSpec)
                        .addImport(PackageName.FEATURE_COMPONENT.value, "FeatureComponent")
                        .build()
                        .writeTo(writer)
                }
            }
        } catch (_: FileAlreadyExistsException) {
            environment.logger.warn("Module file $fileName already exists. Skipping generation.")
        }
    }

    private fun writeModuleIndexToFile(indexSpec: TypeSpec) {
        try {
            val packageName = getGeneratedPackage()

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
        } catch (e: FileAlreadyExistsException) {
            environment.logger.warn("Module index file already exists. Skipping generation.")
        }
    }

    private fun getFeaturePackage() = GeneratedPackages.getFeaturePackage(environment)
    private fun getGeneratedPackage() = GeneratedPackages.getDiPackage(environment)
}