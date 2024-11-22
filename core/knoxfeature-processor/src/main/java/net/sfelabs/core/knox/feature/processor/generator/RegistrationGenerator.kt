package net.sfelabs.core.knox.feature.processor.generator

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import net.sfelabs.core.knox.feature.processor.model.FeatureGroup
import net.sfelabs.core.knox.feature.processor.model.PackageName
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import net.sfelabs.core.knox.feature.domain.model.FeatureCategory
import net.sfelabs.core.knox.feature.processor.utils.*

class RegistrationGenerator(
    private val environment: SymbolProcessorEnvironment
) {
    fun generate(feature: FeatureGroup) {
        val registrationSpec = TypeSpec.classBuilder("${feature.featureName.capitalizeWords()}Registration")
            .addSuperinterface(
                ClassName(PackageName.FEATURE_REGISTRY.value, "FeatureRegistration")
                    .parameterizedBy(feature.configType.toClassName())
            )
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter("getter", getClassName(feature.getter as KSClassDeclaration))
                    .addParameter("setter", getClassName(feature.setter as KSClassDeclaration))
                    .build()
            )
            .addProperty(
                PropertySpec.builder(
                    "key",
                    ClassName(PackageName.FEATURE_MODEL.value, "FeatureKey")
                        .parameterizedBy(feature.configType.toClassName())
                )
                    .initializer("%T()",
                        ClassName(PackageName.FEATURE_GENERATED.value, "${feature.featureName.capitalizeWords()}Key")
                    )
                    .addModifiers(KModifier.OVERRIDE)
                    .build()
            )
            .addProperty(
                PropertySpec.builder(
                    "handler",
                    ClassName(PackageName.FEATURE_HANDLER.value, "FeatureHandler")
                        .parameterizedBy(feature.configType.toClassName())
                )
                    .initializer("%T(getter, setter)",
                        ClassName(PackageName.FEATURE_GENERATED.value, "${feature.featureName.capitalizeWords()}Handler")
                    )
                    .addModifiers(KModifier.OVERRIDE)
                    .build()
            )
            .addProperty(
                PropertySpec.builder("category", FeatureCategory::class)
                    .initializer("%T.%L", FeatureCategory::class, feature.category.name)
                    .addModifiers(KModifier.OVERRIDE)
                    .build()
            )
            .build()

        writeToFile(registrationSpec, "${feature.featureName.capitalizeWords()}Registration")
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