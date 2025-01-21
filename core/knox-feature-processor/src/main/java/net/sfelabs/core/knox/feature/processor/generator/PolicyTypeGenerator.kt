package net.sfelabs.core.knox.feature.processor.generator

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import net.sfelabs.core.domain.usecase.model.ApiError
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.api.*
import net.sfelabs.core.knox.feature.domain.model.Feature
import net.sfelabs.core.knox.feature.processor.model.ProcessedFeature
import net.sfelabs.core.knox.feature.processor.utils.GeneratedPackages
import net.sfelabs.core.knox.feature.processor.utils.toClassName

@Suppress("SameParameterValue")
class PolicyTypeGenerator(
    private val environment: SymbolProcessorEnvironment
) {
    fun generate(features: List<ProcessedFeature>) {
        if (features.isEmpty()) return

        generatePolicyType(features)
        generatePolicyExtensions(features)
    }

    private fun generatePolicyType(features: List<ProcessedFeature>) {
        val policyTypeSpec = TypeSpec.interfaceBuilder("PolicyType")
            .addModifiers(KModifier.SEALED)
            .addSuperinterface(
                ClassName.bestGuess(PolicyDescriptor::class.qualifiedName!!)
                    .parameterizedBy(PolicyState::class.asClassName())
            )

        // Add nested objects for each policy
        features.forEach { feature ->
            val policyObject = TypeSpec.objectBuilder(feature.className)
                .addSuperinterface(ClassName("", "PolicyType"))
                .addSuperinterface(
                    ClassName.bestGuess(PolicyDescriptor::class.qualifiedName!!)
                        .parameterizedBy(feature.valueType.toClassName())
                )
                .addProperty(
                    PropertySpec.builder("key",
                        ClassName.bestGuess(FeatureKey::class.qualifiedName!!)
                            .parameterizedBy(feature.valueType.toClassName())
                    )
                        .addModifiers(KModifier.OVERRIDE)
                        .initializer("%T",
                            ClassName(getGeneratedPackage(), "${feature.className}Key")
                        )
                        .build()
                )
                .addProperty(
                    PropertySpec.builder("component",
                        ClassName.bestGuess(FeatureComponent::class.qualifiedName!!)
                            .parameterizedBy(feature.valueType.toClassName())
                    )
                        .addModifiers(KModifier.OVERRIDE)
                        .initializer(
                            "lazy { %T() }.value",
                            ClassName(getGeneratedPackage(), "${feature.className}Component")
                        )
                        .build()
                )
                .build()

            policyTypeSpec.addType(policyObject)
        }

        // Add companion object with helper methods
        val companionObject = TypeSpec.companionObjectBuilder()
            .addFunction(
                FunSpec.builder("fromFeature")
                    .addParameter(
                        "feature",
                        Feature::class.asClassName().parameterizedBy(WildcardTypeName.producerOf(ANY))
                    )
                    .returns(ClassName("", "PolicyType"))
                    .beginControlFlow("return when (feature.key)")
                    .apply {
                        features.forEach { feature ->
                            addStatement(
                                "is %T -> %L",
                                ClassName(getGeneratedPackage(), "${feature.className}Key"),
                                feature.className
                            )
                        }
                        addStatement("else -> throw IllegalArgumentException(\"Unknown policy type\")")
                    }
                    .endControlFlow()
                    .build()
            )
            .build()

        policyTypeSpec.addType(companionObject)

        writeTypeToFile(policyTypeSpec.build(), "PolicyType")
    }

    private fun generatePolicyExtensions(features: List<ProcessedFeature>) {
        val extensionsSpec = FileSpec.builder(getGeneratedPackage(), "PolicyExtensions")
            .addImport("net.sfelabs.core.domain.usecase.model", "ApiResult", "ApiError", "DefaultApiError")
            // Generic extensions
            .addFunction(generateAsPolicyFunction())
            .addFunction(generateGetTypedStateFunction())
            .addFunction(generateUpdateStateFunction())
            .addFunction(generateIsSupportedFunction())
            .addFunction(generateGetErrorFunction())
            .addFunction(generateHasErrorFunction())
            // Policy-specific extensions
            .apply {
                features.forEach { feature ->
                    addFunction(generatePolicySpecificExtension(feature))
                }
            }
            .build()

        writeToFile(extensionsSpec, "PolicyExtensions")
    }

    private fun generateAsPolicyFunction(): FunSpec {
        return FunSpec.builder("asPolicy")
            .receiver(Feature::class.asClassName().parameterizedBy(WildcardTypeName.producerOf(ANY)))
            .addTypeVariable(TypeVariableName("T", PolicyState::class.asClassName()))
            .addParameter("descriptor", PolicyDescriptor::class.asClassName().parameterizedBy(TypeVariableName("T")))
            .returns(Feature::class.asClassName().parameterizedBy(TypeVariableName("T")).copy(nullable = true))
            .addKdoc("Safely cast a Feature to a specific policy type\n")
            .addCode("""
                return if (this.key == descriptor.key) {
                    @Suppress("UNCHECKED_CAST")
                    this as Feature<T>
                } else null
            """.trimIndent())
            .build()
    }

    private fun generateGetTypedStateFunction(): FunSpec {
        return FunSpec.builder("getTypedState")
            .receiver(Feature::class.asClassName().parameterizedBy(WildcardTypeName.producerOf(ANY)))
            .addTypeVariable(TypeVariableName("T", PolicyState::class.asClassName()))
            .addParameter("descriptor", PolicyDescriptor::class.asClassName().parameterizedBy(TypeVariableName("T")))
            .returns(TypeVariableName("T").copy(nullable = true))
            .addKdoc("Get the typed state for a specific policy\n")
            .addCode("return asPolicy(descriptor)?.state?.value")
            .build()
    }

    private fun generateUpdateStateFunction(): FunSpec {
        return FunSpec.builder("updateState")
            .receiver(Feature::class.asClassName().parameterizedBy(WildcardTypeName.producerOf(ANY)))
            .addTypeVariable(TypeVariableName("T", PolicyState::class.asClassName()))
            .addParameter("descriptor", PolicyDescriptor::class.asClassName().parameterizedBy(TypeVariableName("T")))
            .addParameter(
                "update",
                LambdaTypeName.get(
                    parameters = arrayOf(TypeVariableName("T")),
                    returnType = TypeVariableName("T")
                )
            )
            .returns(ApiResult::class.asClassName().parameterizedBy(Unit::class.asClassName()))
            .addKdoc("Safely update state for a specific policy\n")
            .addCode("""
                val currentState = getTypedState(descriptor) ?: return ApiResult.Error(
                    DefaultApiError.UnexpectedError("Invalid policy state type")
                )
                return descriptor.component.handler.setState(update(currentState))
            """.trimIndent())
            .build()
    }

    private fun generateIsSupportedFunction(): FunSpec {
        return FunSpec.builder("isSupported")
            .receiver(Feature::class.asClassName().parameterizedBy(WildcardTypeName.producerOf(ANY)))
            .addTypeVariable(TypeVariableName("T", PolicyState::class.asClassName()))
            .addParameter("descriptor", PolicyDescriptor::class.asClassName().parameterizedBy(TypeVariableName("T")))
            .returns(Boolean::class)
            .addKdoc("Check if the policy is supported on the current device\n")
            .addCode("return getTypedState(descriptor)?.isSupported ?: false")
            .build()
    }

    private fun generateGetErrorFunction(): FunSpec {
        return FunSpec.builder("getError")
            .receiver(Feature::class.asClassName().parameterizedBy(WildcardTypeName.producerOf(ANY)))
            .addTypeVariable(TypeVariableName("T", PolicyState::class.asClassName()))
            .addParameter("descriptor", PolicyDescriptor::class.asClassName().parameterizedBy(TypeVariableName("T")))
            .returns(
                Pair::class.asClassName().parameterizedBy(
                    ApiError::class.asClassName().copy(nullable = true),
                    Throwable::class.asClassName().copy(nullable = true)
                )
            )
            .addKdoc("Get error information for the policy if any exists\n")
            .addCode("""
                val state = getTypedState(descriptor)
                return Pair(state?.error, state?.exception)
            """.trimIndent())
            .build()
    }

    private fun generateHasErrorFunction(): FunSpec {
        return FunSpec.builder("hasError")
            .receiver(Feature::class.asClassName().parameterizedBy(WildcardTypeName.producerOf(ANY)))
            .returns(Boolean::class)
            .addKdoc("Check if the policy currently has any errors\n")
            .addCode("return state.value.error != null")
            .build()
    }

    private fun generatePolicySpecificExtension(feature: ProcessedFeature): FunSpec {
        return FunSpec.builder("as${feature.className}")
            .receiver(Feature::class.asClassName().parameterizedBy(WildcardTypeName.producerOf(ANY)))
            .returns(
                Feature::class.asClassName()
                    .parameterizedBy(feature.valueType.toClassName())
                    .copy(nullable = true)
            )
            .addKdoc("Cast to ${feature.className} policy type\n")
            .addCode("return asPolicy(PolicyType.${feature.className})")
            .build()
    }

    private fun writeTypeToFile(typeSpec: TypeSpec, fileName: String) {
        try {
            val packageName = getGeneratedPackage()
            environment.codeGenerator.createNewFile(
                Dependencies(false),
                packageName,
                fileName
            ).use { output ->
                output.writer().use { writer ->
                    FileSpec.builder(packageName, fileName)
                        .addType(typeSpec)
                        .build()
                        .writeTo(writer)
                }
            }
        } catch (_: FileAlreadyExistsException) {
            environment.logger.warn("$fileName file already exists. Skipping generation.")
        }
    }

    private fun writeToFile(fileSpec: FileSpec, fileName: String) {
        try {
            val packageName = getGeneratedPackage()
            environment.codeGenerator.createNewFile(
                Dependencies(false),
                packageName,
                fileName
            ).use { output ->
                output.writer().use { writer ->
                    fileSpec.writeTo(writer)
                }
            }
        } catch (_: FileAlreadyExistsException) {
            environment.logger.warn("$fileName file already exists. Skipping generation.")
        }
    }

    private fun getGeneratedPackage(): String =
        GeneratedPackages.getFeaturePackage(environment)
}