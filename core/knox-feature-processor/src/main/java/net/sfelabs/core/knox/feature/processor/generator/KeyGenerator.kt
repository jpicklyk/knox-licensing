package net.sfelabs.core.knox.feature.processor.generator

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import net.sfelabs.core.knox.feature.api.FeatureKey
import net.sfelabs.core.knox.feature.processor.model.ProcessedFeature
import net.sfelabs.core.knox.feature.processor.utils.GeneratedPackages
import net.sfelabs.core.knox.feature.processor.utils.NameUtils.classNameToFeatureName
import net.sfelabs.core.knox.feature.processor.utils.toClassName

class KeyGenerator(
    private val environment: SymbolProcessorEnvironment
) {
    fun generate(features: List<ProcessedFeature>) {
        features.forEach { feature ->
            generateKey(feature)
        }
    }

    private fun generateKey(feature: ProcessedFeature) {
        val keySpec = TypeSpec.objectBuilder("${feature.className}Key")
            .addSuperinterface(
                ClassName.bestGuess(FeatureKey::class.qualifiedName!!)
                    .parameterizedBy(feature.valueType.toClassName())
            )
            .addProperty(
                PropertySpec.builder("featureName", String::class)
                    .addModifiers(KModifier.OVERRIDE)
                    .initializer("%S", classNameToFeatureName(feature.className))
                    .build()
            )
            .build()

        writeToFile(keySpec, feature)
    }

    private fun writeToFile(keySpec: TypeSpec, feature: ProcessedFeature) {
        try {
            val packageName = getGeneratedPackage()

            environment.codeGenerator.createNewFile(
                Dependencies(false),
                packageName,
                "${feature.className}Key"
            ).use { output ->
                output.writer().use { writer ->
                    FileSpec.builder(packageName, "${feature.className}Key")
                        .addType(keySpec)
                        .build()
                        .writeTo(writer)
                }
            }
        } catch (_: FileAlreadyExistsException) {
            environment.logger.warn("Key file already exists for ${feature.className}. Skipping generation.")
        }
    }

    private fun getGeneratedPackage() = GeneratedPackages.getFeaturePackage(environment)
}