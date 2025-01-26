package net.sfelabs.core.knox.feature.processor.generator

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.api.PolicyCategory
import net.sfelabs.core.knox.feature.api.PolicyComponent
import net.sfelabs.core.knox.feature.api.PolicyKey
import net.sfelabs.core.knox.feature.api.PolicyParameters
import net.sfelabs.core.knox.feature.domain.usecase.handler.PolicyHandler
import net.sfelabs.core.knox.feature.processor.model.ProcessedPolicy
import net.sfelabs.core.knox.feature.processor.utils.GeneratedPackages
import net.sfelabs.core.knox.feature.processor.utils.NameUtils.classNameToPolicyName
import net.sfelabs.core.knox.feature.processor.utils.toClassName

class ComponentGenerator(
    private val environment: SymbolProcessorEnvironment
) {
    fun generate(policies: List<ProcessedPolicy>) {
        policies.forEach { policy ->
            generateComponent(policy)
        }
    }

    private fun generateComponent(policy: ProcessedPolicy) {
        val componentSpec = TypeSpec.classBuilder("${policy.className}Component")
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addAnnotation(ClassName.bestGuess("javax.inject.Inject"))
                    .build()
            )
            .addSuperinterface(
                ClassName.bestGuess(PolicyComponent::class.qualifiedName!!)
                    .parameterizedBy(policy.valueType.toClassName())
            )
            .addProperty(
                PropertySpec.builder("policyImpl", ClassName(policy.packageName, policy.className))
                    .addModifiers(KModifier.PRIVATE)
                    .initializer("%T()", ClassName(policy.packageName, policy.className))
                    .build()
            )
            .addProperties(generateComponentProperties(policy))
            .build()

        writeToFile(componentSpec, policy)
    }

    private fun generateComponentProperties(policy: ProcessedPolicy): List<PropertySpec> {
        val stateType = policy.valueType.toClassName()

        return listOf(
            PropertySpec.builder("policyName", String::class)
                .addModifiers(KModifier.OVERRIDE)
                .initializer("%S", classNameToPolicyName(policy.className))
                .build(),

            PropertySpec.builder("title", String::class)
                .addModifiers(KModifier.OVERRIDE)
                .initializer("%S", policy.title)
                .build(),

            PropertySpec.builder("description", String::class)
                .addModifiers(KModifier.OVERRIDE)
                .initializer("%S", policy.description)
                .build(),

            PropertySpec.builder("category", PolicyCategory::class)
                .addModifiers(KModifier.OVERRIDE)
                .initializer("%T.%L", PolicyCategory::class, policy.category.name)
                .build(),

            PropertySpec.builder("handler",
                ClassName.bestGuess(PolicyHandler::class.qualifiedName!!)
                    .parameterizedBy(stateType)  // Use consistent type
            )
                .addModifiers(KModifier.OVERRIDE)
                .initializer(buildHandlerInitializer(policy))
                .build(),

            PropertySpec.builder("defaultValue", stateType)  // Use consistent type
                .addModifiers(KModifier.OVERRIDE)
                .initializer("policyImpl.defaultValue")
                .build(),

            PropertySpec.builder("key",
                ClassName.bestGuess(PolicyKey::class.qualifiedName!!)
                    .parameterizedBy(stateType)  // Use consistent type
            )
                .addModifiers(KModifier.OVERRIDE)
                .initializer(
                    "%T",
                    ClassName(getGeneratedPackage(), "${policy.className}Key")
                )
                .build()
        )
    }

    private fun buildHandlerInitializer(policy: ProcessedPolicy): CodeBlock {
        val stateType = policy.valueType.toClassName()

        return CodeBlock.builder()
            .beginControlFlow(
                "object : %T<%T>",
                ClassName.bestGuess(PolicyHandler::class.qualifiedName!!),
                stateType  // Use specific type
            )
            .beginControlFlow(
                "override suspend fun getState(parameters: %T): %T",
                ClassName.bestGuess(PolicyParameters::class.qualifiedName!!),
                stateType  // Use specific type
            )
            .addStatement("return policyImpl.getState(parameters)")
            .endControlFlow()
            .beginControlFlow(
                "override suspend fun setState(newState: %T): %T<Unit>",
                stateType,  // Use specific type
                ClassName.bestGuess(ApiResult::class.qualifiedName!!)
            )
            .addStatement("return policyImpl.setState(newState)")
            .endControlFlow()
            .endControlFlow()
            .build()
    }

    private fun writeToFile(componentSpec: TypeSpec, policy: ProcessedPolicy) {
        try {
            val packageName = getGeneratedPackage()

            environment.codeGenerator.createNewFile(
                Dependencies(false),
                packageName,
                "${policy.className}Component"
            ).use { output ->
                output.writer().use { writer ->
                    FileSpec.builder(packageName, "${policy.className}Component")
                        .addType(componentSpec)
                        .build()
                        .writeTo(writer)
                }
            }
        } catch (_: FileAlreadyExistsException) {
            environment.logger.warn("Component file already exists for ${policy.className}. Skipping generation.")
        }
    }

    private fun getGeneratedPackage(): String =
        GeneratedPackages.getPolicyPackage(environment)
}