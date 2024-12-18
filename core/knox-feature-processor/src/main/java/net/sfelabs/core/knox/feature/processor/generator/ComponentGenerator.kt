package net.sfelabs.core.knox.feature.processor.generator

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.processor.model.PackageName
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
                    .addAnnotation(ClassName("javax.inject", "Inject"))
                    .build()
            )
            .addSuperinterface(
                ClassName(PackageName.FEATURE_COMPONENT.value, "FeatureComponent")
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
                ClassName(PackageName.FEATURE_HANDLER.value, "FeatureHandler")
                    .parameterizedBy(feature.valueType.toClassName())
            )
                .addModifiers(KModifier.OVERRIDE)
                .initializer(
                    buildHandlerInitializer(feature)
                )
                .build(),

            PropertySpec.builder("defaultValue", feature.valueType.toClassName())
                .addModifiers(KModifier.OVERRIDE)
                .initializer(
                    try {
                        getDefaultValueForType(feature.valueType)
                    } catch (e: IllegalStateException) {
                        "featureImpl.defaultValue"
                    }
                )
                .build(),

            PropertySpec.builder("key",
                ClassName(PackageName.FEATURE_PUBLIC.value, "FeatureKey")
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
                ClassName(PackageName.FEATURE_HANDLER.value, "FeatureHandler"),
                feature.valueType.toClassName()
            )
            .beginControlFlow(
                "override suspend fun getState(parameters: %T): %T<%T<%T>>",
                ClassName(PackageName.FEATURE_PUBLIC.value, "FeatureParameters"),
                ClassName(PackageName.API_DOMAIN.value, "ApiResult"),
                ClassName(PackageName.FEATURE_MODEL.value, "FeatureState"),
                feature.valueType.toClassName()
            )
            .addStatement(
                "return featureImpl.getState(parameters).wrapInFeatureState(%T.%L)",
                ClassName(PackageName.FEATURE_COMPONENT.value, "StateMapping"),
                feature.stateMapping.name
            )
            .endControlFlow()
            .beginControlFlow(
                "override suspend fun setState(newState: %T<%T>): %T<Unit>",
                ClassName(PackageName.FEATURE_MODEL.value, "FeatureState"),
                feature.valueType.toClassName(),
                ClassName(PackageName.API_DOMAIN.value, "ApiResult")
            )
            .addStatement("return featureImpl.setState(newState.value)")
            .endControlFlow()
            .endControlFlow()
            .build()
    }

    private fun getDefaultValueForType(type: KSType): String {
        return when (type.declaration.simpleName.asString()) {
            "Boolean" -> "false"
            "Int" -> "0"
            "Long" -> "0L"
            "Float" -> "0f"
            "Double" -> "0.0"
            "String" -> "\"\""
            else -> throw IllegalStateException(
                """
            No default value defined for type: ${type.declaration.simpleName.asString()}
            For complex types, you must provide a default value by implementing the defaultValue property in your FeatureContract:
            
            class YourFeature : FeatureContract<YourType> {
                override val defaultValue = YourType(...)  // Provide your default instance
                // ... rest of implementation
            }
            """.trimIndent()
            )
        }
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
                        .addImport(PackageName.FEATURE_COMPONENT.value, "FeatureComponent")
                        .addImport(PackageName.FEATURE_HANDLER.value, "FeatureHandler")
                        .addImport(PackageName.FEATURE_PUBLIC.value, "FeatureCategory")
                        .addImport(PackageName.FEATURE_MODEL.value, "FeatureState")
                        .addImport(PackageName.FEATURE_PUBLIC.value, "FeatureParameters")
                        .addImport(PackageName.FEATURE_MODEL.value, "wrapInFeatureState")
                        .addImport(PackageName.API_DOMAIN.value, "map")
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