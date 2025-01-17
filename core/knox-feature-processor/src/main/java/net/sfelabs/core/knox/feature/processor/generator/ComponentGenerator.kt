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
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.FeatureComponent
import net.sfelabs.core.knox.feature.api.FeatureKey
import net.sfelabs.core.knox.feature.api.FeatureParameters
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.core.knox.feature.domain.usecase.handler.FeatureHandler
import net.sfelabs.core.knox.feature.processor.model.ProcessedFeature
import net.sfelabs.core.knox.feature.processor.utils.GeneratedPackages
import net.sfelabs.core.knox.feature.processor.utils.NameUtils.classNameToFeatureName
import net.sfelabs.core.knox.feature.processor.utils.toClassName

class ComponentGenerator(
    private val environment: SymbolProcessorEnvironment
) {
    fun generate(features: List<ProcessedFeature>) {
        features.forEach { feature ->
            generateComponent(feature)
        }
    }

    private fun generateComponent(feature: ProcessedFeature) {
        val componentSpec = TypeSpec.classBuilder("${feature.className}Component")
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addAnnotation(ClassName.bestGuess("javax.inject.Inject"))
                    .build()
            )
            .addSuperinterface(
                ClassName.bestGuess(FeatureComponent::class.qualifiedName!!)
                    .parameterizedBy(feature.valueType.toClassName())
            )
            .addProperty(
                PropertySpec.builder("featureImpl", ClassName(feature.packageName, feature.className))
                    .addModifiers(KModifier.PRIVATE)
                    .initializer("%T()", ClassName(feature.packageName, feature.className))
                    .build()
            )
            .addProperties(generateComponentProperties(feature))
            .build()

        writeToFile(componentSpec, feature)
    }

    private fun generateComponentProperties(feature: ProcessedFeature): List<PropertySpec> {
        return listOf(
            PropertySpec.builder("featureName", String::class)
                .addModifiers(KModifier.OVERRIDE)
                .initializer("%S", classNameToFeatureName(feature.className))
                .build(),

            PropertySpec.builder("title", String::class)
                .addModifiers(KModifier.OVERRIDE)
                .initializer("%S", feature.title)
                .build(),

            PropertySpec.builder("description", String::class)
                .addModifiers(KModifier.OVERRIDE)
                .initializer("%S", feature.description)
                .build(),

            PropertySpec.builder("category", FeatureCategory::class)
                .addModifiers(KModifier.OVERRIDE)
                .initializer("%T.%L", FeatureCategory::class, feature.category.name)
                .build(),

            PropertySpec.builder("handler",
                ClassName.bestGuess(FeatureHandler::class.qualifiedName!!)
                    .parameterizedBy(feature.valueType.toClassName())
            )
                .addModifiers(KModifier.OVERRIDE)
                .initializer(
                    buildHandlerInitializer(feature)
                )
                .build(),

            PropertySpec.builder("defaultValue", feature.valueType.toClassName())
                .addModifiers(KModifier.OVERRIDE)
                .initializer("featureImpl.defaultValue")
                .build(),

            PropertySpec.builder("key",
                ClassName.bestGuess(FeatureKey::class.qualifiedName!!)
                    .parameterizedBy(feature.valueType.toClassName())
            )
                .addModifiers(KModifier.OVERRIDE)
                .initializer(
                    "%T",
                    ClassName(getGeneratedPackage(), "${feature.className}Key")
                )
                .build()
        )
    }

    private fun buildHandlerInitializer(feature: ProcessedFeature): CodeBlock {
        return CodeBlock.builder()
            .beginControlFlow(
                "object : %T<%T>",
                ClassName.bestGuess(FeatureHandler::class.qualifiedName!!),
                feature.valueType.toClassName()
            )
            .beginControlFlow(
                "override suspend fun getState(parameters: %T): %T",
                ClassName.bestGuess(FeatureParameters::class.qualifiedName!!),
                feature.valueType.toClassName()
            )
            .addStatement(
                """
            val result = featureImpl.getState(parameters)
            // Apply state mapping to the enabled state
            val mappedEnabled = when(${StateMapping::class.qualifiedName}.${feature.stateMapping.name}) {
                ${StateMapping::class.qualifiedName}.DIRECT -> result.isEnabled
                ${StateMapping::class.qualifiedName}.INVERTED -> !result.isEnabled
                ${StateMapping::class.qualifiedName}.CUSTOM -> {
                    try {
                        val companionClass = Class.forName("${feature.packageName}.${feature.className}.Companion")
                        val mapStateMethod = companionClass.getDeclaredMethod("mapState", Boolean::class.java)
                        mapStateMethod.invoke(null, result.isEnabled) as Boolean
                    } catch (_: Exception) {
                        result.isEnabled
                    }
                }
            }
            return result.copy(isEnabled = mappedEnabled)
            """.trimIndent()
            )
            .endControlFlow()
            .beginControlFlow(
                "override suspend fun setState(newState: %T): %T<Unit>",
                feature.valueType.toClassName(),
                ClassName.bestGuess(ApiResult::class.qualifiedName!!)
            )
            .addStatement("return featureImpl.setState(newState)")
            .endControlFlow()
            .endControlFlow()
            .build()
    }

    private fun writeToFile(componentSpec: TypeSpec, feature: ProcessedFeature) {
        try {
            val packageName = getGeneratedPackage()

            environment.codeGenerator.createNewFile(
                Dependencies(false),
                packageName,
                "${feature.className}Component"
            ).use { output ->
                output.writer().use { writer ->
                    FileSpec.builder(packageName, "${feature.className}Component")
                        .addType(componentSpec)
                        // No need for imports as we're using fully qualified names
                        .build()
                        .writeTo(writer)
                }
            }
        } catch (_: FileAlreadyExistsException) {
            environment.logger.warn("Component file already exists for ${feature.className}. Skipping generation.")
        }
    }

    private fun getGeneratedPackage(): String =
        GeneratedPackages.getFeaturePackage(environment)
}