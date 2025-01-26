package net.sfelabs.core.knox.feature.processor.generator

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import net.sfelabs.core.knox.feature.api.PolicyKey
import net.sfelabs.core.knox.feature.processor.model.ProcessedPolicy
import net.sfelabs.core.knox.feature.processor.utils.GeneratedPackages
import net.sfelabs.core.knox.feature.processor.utils.NameUtils.classNameToPolicyName
import net.sfelabs.core.knox.feature.processor.utils.toClassName

class KeyGenerator(
    private val environment: SymbolProcessorEnvironment
) {
    fun generate(policies: List<ProcessedPolicy>) {
        policies.forEach { policy ->
            generateKey(policy)
        }
    }

    private fun generateKey(policy: ProcessedPolicy) {
        val keySpec = TypeSpec.objectBuilder("${policy.className}Key")
            .addSuperinterface(
                ClassName.bestGuess(PolicyKey::class.qualifiedName!!)
                    .parameterizedBy(policy.valueType.toClassName())
            )
            .addProperty(
                PropertySpec.builder("policyName", String::class)
                    .addModifiers(KModifier.OVERRIDE)
                    .initializer("%S", classNameToPolicyName(policy.className))
                    .build()
            )
            .build()

        writeToFile(keySpec, policy)
    }

    private fun writeToFile(keySpec: TypeSpec, policy: ProcessedPolicy) {
        try {
            val packageName = getGeneratedPackage()

            environment.codeGenerator.createNewFile(
                Dependencies(false),
                packageName,
                "${policy.className}Key"
            ).use { output ->
                output.writer().use { writer ->
                    FileSpec.builder(packageName, "${policy.className}Key")
                        .addType(keySpec)
                        .build()
                        .writeTo(writer)
                }
            }
        } catch (_: FileAlreadyExistsException) {
            environment.logger.warn("Key file already exists for ${policy.className}. Skipping generation.")
        }
    }

    private fun getGeneratedPackage() = GeneratedPackages.getPolicyPackage(environment)
}