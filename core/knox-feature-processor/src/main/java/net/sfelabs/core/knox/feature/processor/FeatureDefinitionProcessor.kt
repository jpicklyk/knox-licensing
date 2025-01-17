package net.sfelabs.core.knox.feature.processor

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.processor.generator.ComponentGenerator
import net.sfelabs.core.knox.feature.processor.generator.KeyGenerator
import net.sfelabs.core.knox.feature.processor.generator.ModuleGenerator
import net.sfelabs.core.knox.feature.processor.model.ProcessedFeature

class FeatureDefinitionProcessor(
    val environment: SymbolProcessorEnvironment
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        // Collect all classes with @FeatureDefinition
        val featureClasses = resolver.getSymbolsWithAnnotation("net.sfelabs.core.knox.feature.annotation.FeatureDefinition")
            .filterIsInstance<KSClassDeclaration>()
            .mapNotNull { processFeatureDefinition(it) }
            .toList()

        if (featureClasses.isNotEmpty()) {
            // Generate code for each feature
            ComponentGenerator(environment).generate(featureClasses)
            KeyGenerator(environment).generate(featureClasses)
            ModuleGenerator(environment).generate(featureClasses)
        }

        return emptyList()
    }

    private fun findFeatureContractType(classDeclaration: KSClassDeclaration): KSType? {
        for (superType in classDeclaration.superTypes) {
            val qualifiedName = superType.resolve().declaration.qualifiedName?.asString()

            if (qualifiedName == "net.sfelabs.core.knox.feature.api.FeatureContract") {
                return superType.resolve()
            }

            // Check superTypes of this type recursively
            val superDecl = superType.resolve().declaration as? KSClassDeclaration
            if (superDecl != null) {
                findFeatureContractType(superDecl)?.let { return it }
            }
        }
        return null
    }

    private fun processFeatureDefinition(classDeclaration: KSClassDeclaration): ProcessedFeature? {
        val annotation = classDeclaration.annotations.find {
            it.shortName.asString() == "FeatureDefinition"
        } ?: return null

        // Get the type parameter T from FeatureContract<T>
        val contractType = findFeatureContractType(classDeclaration) ?: return null
        val valueType = contractType.arguments.firstOrNull()?.type?.resolve() ?: return null

        // Verify the type implements PolicyState
        val hasPolicyStateInterface =
            (valueType.declaration as? KSClassDeclaration)?.superTypes?.any { superType ->
                superType.resolve().declaration.qualifiedName?.asString() == "net.sfelabs.core.knox.feature.api.PolicyState"
            } == true

        if (!hasPolicyStateInterface) {
            environment.logger.error(
                "Feature state type ${valueType.declaration.qualifiedName?.asString()} must implement PolicyState",
                valueType.declaration
            )
            return null
        }

        return ProcessedFeature(
            className = classDeclaration.simpleName.asString(),
            packageName = classDeclaration.packageName.asString(),
            title = annotation.arguments
                .find { it.name?.asString() == "title" }
                ?.value as? String ?: return null,
            description = annotation.arguments
                .find { it.name?.asString() == "description" }
                ?.value as? String ?: return null,
            category = (annotation.arguments
                .find { it.name?.asString() == "category" }
                ?.value as? KSType)?.let {
                FeatureCategory.valueOf(it.declaration.simpleName.asString())
            } ?: return null,
            stateMapping = (annotation.arguments
                .find { it.name?.asString() == "stateMapping" }
                ?.value as? KSType)?.let {
                StateMapping.valueOf(it.declaration.simpleName.asString())
            } ?: StateMapping.DIRECT,
            valueType = valueType,
            declaration = classDeclaration
        )
    }
}