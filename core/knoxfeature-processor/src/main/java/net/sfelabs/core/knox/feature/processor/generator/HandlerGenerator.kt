package net.sfelabs.core.knox.feature.processor.generator

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import net.sfelabs.core.knox.feature.processor.model.FeatureGroup
import net.sfelabs.core.knox.feature.processor.model.PackageName
import net.sfelabs.core.knox.feature.processor.utils.capitalizeWords
import net.sfelabs.core.knox.feature.processor.utils.getClassName
import net.sfelabs.core.knox.feature.processor.utils.toClassName

class HandlerGenerator(
    private val environment: SymbolProcessorEnvironment
) {
    fun generate(feature: FeatureGroup) {
        val handlerSpec = TypeSpec.classBuilder("${feature.featureName.capitalizeWords()}Handler")
            .addSuperinterface(
                ClassName(PackageName.FEATURE_HANDLER.value, "FeatureHandler")
                    .parameterizedBy(feature.configType.toClassName())
            )
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter(
                        "getter",
                        getClassName(feature.getter as KSClassDeclaration)
                    )
                    .addParameter(
                        "setter",
                        getClassName(feature.setter as KSClassDeclaration)
                    )
                    .build()
            )
            .addProperty(
                PropertySpec.builder("getter", getClassName(feature.getter as KSClassDeclaration))
                    .initializer("getter")
                    .addModifiers(KModifier.PRIVATE)
                    .build()
            )
            .addProperty(
                PropertySpec.builder("setter", getClassName(feature.setter as KSClassDeclaration))
                    .initializer("setter")
                    .addModifiers(KModifier.PRIVATE)
                    .build()
            )
            .addFunction(generateGetStateFunction(feature))
            .addFunction(generateSetStateFunction(feature))
            .build()

        writeToFile(handlerSpec, "${feature.featureName.capitalizeWords()}Handler")
    }

    private fun generateGetStateFunction(feature: FeatureGroup): FunSpec {
        return FunSpec.builder("getState")
            .addModifiers(KModifier.OVERRIDE, KModifier.SUSPEND)
            .returns(
                ClassName(PackageName.API_DOMAIN.value, "ApiResult")
                    .parameterizedBy(
                        ClassName(PackageName.FEATURE_MODEL.value, "FeatureState")
                            .parameterizedBy(feature.configType.toClassName())
                    )
            )
            .addCode("return getter(Unit).wrapInFeatureState()")
            .build()
    }

    private fun generateSetStateFunction(feature: FeatureGroup): FunSpec {
        return FunSpec.builder("setState")
            .addModifiers(KModifier.OVERRIDE, KModifier.SUSPEND)
            .addParameter(
                "newState",
                ClassName(PackageName.FEATURE_MODEL.value, "FeatureState")
                    .parameterizedBy(feature.configType.toClassName())
            )
            .returns(
                ClassName(PackageName.API_DOMAIN.value, "ApiResult")
                    .parameterizedBy(ClassName("kotlin", "Unit"))
            )
            .addCode("return setter(newState.value)")
            .build()
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
                        .addImport(PackageName.API_DOMAIN.value, "ApiResult")
                        .addImport(PackageName.FEATURE_MODEL.value, "FeatureState")
                        .addImport(PackageName.FEATURE_HANDLER.value, "FeatureHandler")
                        .addImport(PackageName.FEATURE_MODEL.value, "wrapInFeatureState")
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