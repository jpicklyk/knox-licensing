package net.sfelabs.core.knox.feature.processor.generator

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import net.sfelabs.core.domain.usecase.model.ApiError
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.api.*
import net.sfelabs.core.knox.feature.domain.model.Policy
import net.sfelabs.core.knox.feature.processor.model.ProcessedPolicy
import net.sfelabs.core.knox.feature.processor.utils.GeneratedPackages
import net.sfelabs.core.knox.feature.processor.utils.toClassName

@Suppress("SameParameterValue")
class PolicyTypeGenerator(
    private val environment: SymbolProcessorEnvironment
) {
    fun generate(policies: List<ProcessedPolicy>) {
        if (policies.isEmpty()) return

        generatePolicyType(policies)
        generatePolicyExtensions(policies)
    }

    private fun generatePolicyType(policies: List<ProcessedPolicy>) {
        val policyTypeSpec = TypeSpec.interfaceBuilder("PolicyType")
            .addModifiers(KModifier.SEALED)
            .addTypeVariable(
                TypeVariableName(
                    name = "T",
                    bounds = listOf(PolicyState::class.asClassName())
                )
            )
            .addSuperinterface(
                PolicyDescriptor::class.asClassName()
                    .parameterizedBy(TypeVariableName("T"))
            )

        // Add nested objects for each policy
        policies.forEach { policy ->
            val policyObject = TypeSpec.objectBuilder(policy.className)
                .addSuperinterface(
                    ClassName("", "PolicyType")
                        .parameterizedBy(policy.valueType.toClassName())
                )
                .addProperty(
                    PropertySpec.builder("key",
                        PolicyKey::class.asClassName()
                            .parameterizedBy(policy.valueType.toClassName())
                    )
                        .addModifiers(KModifier.OVERRIDE)
                        .initializer("%T",
                            ClassName(getGeneratedPackage(), "${policy.className}Key")
                        )
                        .build()
                )
                .addProperty(
                    PropertySpec.builder("component",
                        PolicyComponent::class.asClassName()
                            .parameterizedBy(policy.valueType.toClassName())
                    )
                        .addModifiers(KModifier.OVERRIDE)
                        .initializer(
                            "lazy { %T() }.value",
                            ClassName(getGeneratedPackage(), "${policy.className}Component")
                        )
                        .build()
                )
                .build()

            policyTypeSpec.addType(policyObject)
        }

        // Add companion object with helper methods
        val companionObject = TypeSpec.companionObjectBuilder()
            .addFunction(
                FunSpec.builder("fromPolicy")
                    .addParameter(
                        "policy",
                        Policy::class.asClassName().parameterizedBy(PolicyState::class.asClassName())
                    )
                    .returns(
                        ClassName("", "PolicyType")
                            .parameterizedBy(
                                WildcardTypeName.producerOf(PolicyState::class.asClassName())
                            )

                    )
                    .beginControlFlow("return when (policy.key)")
                    .apply {
                        policies.forEach { policy ->
                            addStatement(
                                "is %T -> %L",
                                ClassName(getGeneratedPackage(), "${policy.className}Key"),
                                policy.className
                            )
                        }
                        addStatement("else -> throw IllegalArgumentException(\"Unknown policy type\")")
                    }
                    .endControlFlow()
                    .build()
            )
            .build()

        policyTypeSpec.addType(companionObject)

        // Write the file
        writeTypeToFile(policyTypeSpec.build(), "PolicyType")
    }

    private fun generatePolicyExtensions(policies: List<ProcessedPolicy>) {
        val extensionsSpec = FileSpec.builder(getGeneratedPackage(), "PolicyExtensions")
            .addImport("net.sfelabs.core.domain.usecase.model", "ApiResult", "ApiError", "DefaultApiError")
            .addFunction(generateAsPolicyFunction())
            .addFunction(generateGetTypedStateFunction())
            .addFunction(generateUpdateStateFunction())
            .addFunction(generateIsSupportedFunction())
            .addFunction(generateGetErrorFunction())
            .addFunction(generateHasErrorFunction())
            .apply {
                policies.forEach { policy ->
                    addFunction(generatePolicySpecificExtension(policy))
                }
            }
            .build()

        writeToFile(extensionsSpec, "PolicyExtensions")
    }

    private fun generateAsPolicyFunction(): FunSpec {
        return FunSpec.builder("asPolicy")
            .receiver(
                Policy::class.asClassName()
                    .parameterizedBy(PolicyState::class.asClassName())
            )
            .addTypeVariable(TypeVariableName("T", PolicyState::class.asClassName()))
            .addParameter("descriptor", PolicyDescriptor::class.asClassName().parameterizedBy(TypeVariableName("T")))
            .returns(Policy::class.asClassName().parameterizedBy(TypeVariableName("T")).copy(nullable = true))
            .addKdoc("Safely cast a Policy to a specific policy type\n")
            .addCode("""
            return if (this.key == descriptor.key) {
                @Suppress("UNCHECKED_CAST")
                this as Policy<T>
            } else null
        """.trimIndent())
            .build()
    }

    private fun generateGetTypedStateFunction(): FunSpec {
        return FunSpec.builder("getTypedState")
            .receiver(
                Policy::class.asClassName()
                    .parameterizedBy(PolicyState::class.asClassName())
            )
            .addTypeVariable(TypeVariableName("T", PolicyState::class.asClassName()))
            .addParameter("descriptor", PolicyDescriptor::class.asClassName().parameterizedBy(TypeVariableName("T")))
            .returns(TypeVariableName("T").copy(nullable = true))
            .addKdoc("Get the typed state for a specific policy\n")
            .addCode("return asPolicy(descriptor)?.state?.value")
            .build()
    }

    private fun generateUpdateStateFunction(): FunSpec {
        return FunSpec.builder("updateState")
            .addModifiers(KModifier.SUSPEND)
            .receiver(
                Policy::class.asClassName()
                    .parameterizedBy(PolicyState::class.asClassName())
            )
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
            .receiver(
                Policy::class.asClassName()
                    .parameterizedBy(PolicyState::class.asClassName())
            )
            .addTypeVariable(TypeVariableName("T", PolicyState::class.asClassName()))
            .addParameter("descriptor", PolicyDescriptor::class.asClassName().parameterizedBy(TypeVariableName("T")))
            .returns(Boolean::class)
            .addKdoc("Check if the policy is supported on the current device\n")
            .addCode("return getTypedState(descriptor)?.isSupported ?: false")
            .build()
    }

    private fun generateGetErrorFunction(): FunSpec {
        return FunSpec.builder("getError")
            .receiver(
                Policy::class.asClassName()
                    .parameterizedBy(PolicyState::class.asClassName())
            )
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
            .receiver(
                Policy::class.asClassName()
                    .parameterizedBy(PolicyState::class.asClassName())
            )
            .returns(Boolean::class)
            .addKdoc("Check if the policy currently has any errors\n")
            .addCode("return state.value.error != null")
            .build()
    }

    private fun generatePolicySpecificExtension(policy: ProcessedPolicy): FunSpec {
        return FunSpec.builder("as${policy.className}")
            .receiver(
                Policy::class.asClassName()
                    .parameterizedBy(PolicyState::class.asClassName())
            )
            .returns(
                Policy::class.asClassName()
                    .parameterizedBy(policy.valueType.toClassName())
                    .copy(nullable = true)
            )
            .addKdoc("Cast to ${policy.className} policy type\n")
            .addCode("return asPolicy(PolicyType.${policy.className})")
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
        GeneratedPackages.getPolicyPackage(environment)
}