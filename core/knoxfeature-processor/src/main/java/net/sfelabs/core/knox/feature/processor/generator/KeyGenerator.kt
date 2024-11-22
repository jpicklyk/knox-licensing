package net.sfelabs.core.knox.feature.processor.generator

import com.google.devtools.ksp.processing.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import net.sfelabs.core.knox.feature.processor.model.FeatureGroup
import net.sfelabs.core.knox.feature.processor.model.PackageName
import net.sfelabs.core.knox.feature.processor.utils.capitalizeWords
import net.sfelabs.core.knox.feature.processor.utils.toClassName

class KeyGenerator(
    private val environment: SymbolProcessorEnvironment
) {
    fun generate(feature: FeatureGroup) {
        val keySpec = TypeSpec.classBuilder("${feature.featureName.capitalizeWords()}Key")
            .addSuperinterface(
                ClassName(PackageName.FEATURE_MODEL.value, "FeatureKey")
                    .parameterizedBy(feature.configType.toClassName())
            )
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter(
                        ParameterSpec.builder("featureName", String::class)
                            .defaultValue("%S", feature.featureName)
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

        writeToFile(keySpec, "${feature.featureName.capitalizeWords()}Key")
    }

    private fun writeToFile(typeSpec: TypeSpec, fileName: String) {
        try {
            val file = environment.codeGenerator.createNewFile(
                Dependencies(false),
                PackageName.FEATURE_GENERATED.value,
                fileName
            )
                file.use { output ->
                output.writer().use { writer ->
                    FileSpec.builder(PackageName.FEATURE_GENERATED.value, fileName)
                        .addImport(PackageName.FEATURE_MODEL.value, "FeatureKey")
                        .addType(typeSpec)
                        .build()
                        .writeTo(writer)
                }
            }
        } catch (_: FileAlreadyExistsException) {
            environment.logger.warn("File $fileName already exists. Skipping generation.")
        }
    }
}