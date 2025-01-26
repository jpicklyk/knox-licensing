package net.sfelabs.core.knox.feature.processor.generator

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.asClassName
import net.sfelabs.core.knox.feature.api.PolicyState
import net.sfelabs.core.knox.feature.processor.model.PackageName
import net.sfelabs.core.knox.feature.processor.model.ProcessedPolicy
import net.sfelabs.core.knox.feature.processor.utils.GeneratedPackages

class ModuleGenerator(
    private val environment: SymbolProcessorEnvironment
) {
    fun generate(policies: List<ProcessedPolicy>) {
        if (policies.isEmpty()) return

        policies.forEach { policy ->
            generateFeatureModule(policy)
        }
        generatePolicyRegistryModule()
        generateModuleIndex(policies)
    }

    private fun generateFeatureModule(policy: ProcessedPolicy) {
        val moduleSpec = TypeSpec.classBuilder("${policy.className}Module")
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
                        ClassName(getGeneratedPackage(), "${policy.className}Component")
                    )
                    .returns(
                        ClassName(PackageName.FEATURE_PUBLIC.value, "PolicyComponent")
                            .parameterizedBy(
                                WildcardTypeName.producerOf(PolicyState::class.asClassName())
                            )
                    )
                    .build()
            )
            .build()

        writeModuleToFile(moduleSpec, policy)
    }

    private fun generateModuleIndex(policies: List<ProcessedPolicy>) {
        if (policies.isEmpty()) return

        val packageName = getGeneratedPackage()
        val moduleNames = policies.map { "${it.className}Module" } + "PolicyRegistryModule"

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

    private fun generatePolicyRegistryModule() {
        val moduleSpec = TypeSpec.classBuilder("PolicyRegistryModule")
            .addModifiers(KModifier.ABSTRACT)
            .addAnnotation(ClassName("dagger", "Module"))
            .addAnnotation(
                AnnotationSpec.builder(ClassName("dagger.hilt", "InstallIn"))
                    .addMember("%T::class", ClassName("dagger.hilt.components", "SingletonComponent"))
                    .build()
            )
            .addFunction(
                FunSpec.builder("bindPolicyRegistry")
                    .addModifiers(KModifier.ABSTRACT)
                    .addAnnotation(ClassName("dagger", "Binds"))
                    .addAnnotation(ClassName("javax.inject", "Singleton"))
                    .addParameter(
                        "impl",
                        ClassName(PackageName.FEATURE_HILT.value, "HiltPolicyRegistry")
                    )
                    .returns(
                        ClassName(PackageName.FEATURE_REGISTRY.value, "PolicyRegistry")
                    )
                    .build()
            )
            .build()

        writeRegistryModuleToFile(moduleSpec)
    }

    private fun writeModuleToFile(
        moduleSpec: TypeSpec,
        policy: ProcessedPolicy
    ) {
        val moduleName = policy.className + "Module"
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
                        .addImport(PackageName.FEATURE_PUBLIC.value, "PolicyComponent")
                        .addImport(PackageName.FEATURE_PUBLIC.value, "PolicyState")
                        .addImport(
                            getFeaturePackage(),
                            policy.className + "Component"
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
        fileName: String = "PolicyRegistryModule"
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
                        .addImport(PackageName.FEATURE_PUBLIC.value, "PolicyComponent")
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
        } catch (_: FileAlreadyExistsException) {
            environment.logger.warn("Module index file already exists. Skipping generation.")
        }
    }

    private fun getFeaturePackage() = GeneratedPackages.getPolicyPackage(environment)
    private fun getGeneratedPackage() = GeneratedPackages.getDiPackage(environment)
}