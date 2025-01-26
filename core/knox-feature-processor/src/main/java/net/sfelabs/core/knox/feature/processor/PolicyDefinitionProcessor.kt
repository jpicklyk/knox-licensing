package net.sfelabs.core.knox.feature.processor

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import net.sfelabs.core.knox.feature.api.PolicyCategory
import net.sfelabs.core.knox.feature.api.PolicyState
import net.sfelabs.core.knox.feature.processor.generator.ComponentGenerator
import net.sfelabs.core.knox.feature.processor.generator.KeyGenerator
import net.sfelabs.core.knox.feature.processor.generator.ModuleGenerator
import net.sfelabs.core.knox.feature.processor.generator.PolicyTypeGenerator
import net.sfelabs.core.knox.feature.processor.model.ProcessedPolicy


class PolicyDefinitionProcessor(
    val environment: SymbolProcessorEnvironment
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val featureClasses = resolver.getSymbolsWithAnnotation("net.sfelabs.core.knox.feature.annotation.PolicyDefinition")
            .filterIsInstance<KSClassDeclaration>()
            .mapNotNull { processFeatureDefinition(it) }
            .toList()

        if (featureClasses.isNotEmpty()) {
            ComponentGenerator(environment).generate(featureClasses)
            KeyGenerator(environment).generate(featureClasses)
            ModuleGenerator(environment).generate(featureClasses)
            PolicyTypeGenerator(environment).generate(featureClasses)
        }

        return emptyList()
    }

    private fun findFeatureContractType(classDeclaration: KSClassDeclaration): KSType? {
        for (superType in classDeclaration.superTypes) {
            val qualifiedName = superType.resolve().declaration.qualifiedName?.asString()

            if (qualifiedName == "net.sfelabs.core.knox.feature.api.FeatureContract") {
                return superType.resolve()
            }

            val superDecl = superType.resolve().declaration as? KSClassDeclaration
            if (superDecl != null) {
                findFeatureContractType(superDecl)?.let { return it }
            }
        }
        return null
    }

    private fun findConfigurableType(classDeclaration: KSClassDeclaration): KSType? {
        for (superType in classDeclaration.superTypes) {
//            val qualifiedName = superType.resolve().declaration.qualifiedName?.asString()
//
//            if (qualifiedName == ConfigurablePolicy::class.qualifiedName) {
//                return superType.resolve()
//            }

            val superDecl = superType.resolve().declaration as? KSClassDeclaration
            if (superDecl != null) {
                findConfigurableType(superDecl)?.let { return it }
            }
        }
        return null
    }

    private fun processFeatureDefinition(
        classDeclaration: KSClassDeclaration
    ): ProcessedPolicy? {
        val annotation = classDeclaration.annotations.find {
            it.shortName.asString() == "PolicyDefinition"
        } ?: return null

        val contractType = findFeatureContractType(classDeclaration) ?: return null
        val valueType = when {
            // If it's ConfigurableStatePolicy, we need to resolve the actual type
            classDeclaration.superTypes.any { superType ->
                superType.resolve().declaration.qualifiedName?.asString()?.startsWith(
                    "net.sfelabs.core.knox.feature.api.ConfigurableStatePolicy"
                ) == true
            } -> {
                // Get the type argument from ConfigurableStatePolicy
                classDeclaration.superTypes
                    .firstOrNull { superType ->
                        superType.resolve().declaration.qualifiedName?.asString()?.startsWith(
                            "net.sfelabs.core.knox.feature.api.ConfigurableStatePolicy"
                        ) == true
                    }?.resolve()?.arguments?.firstOrNull()?.type?.resolve()
                    ?: contractType.arguments.firstOrNull()?.type?.resolve()
                    ?: return null
            }
            else -> contractType.arguments.firstOrNull()?.type?.resolve() ?: return null
        }

        val configurableType = findConfigurableType(classDeclaration)

        // Get configuration type from ConfigurablePolicy if it exists
        val configType = configurableType?.arguments?.get(1)?.type?.resolve()

        // Verify PolicyState implementation
        val hasPolicyStateInterface = when {
            // If it's ConfigurableStatePolicy, skip validation
            classDeclaration.superTypes.any { superType ->
                superType.resolve().declaration.qualifiedName?.asString()?.startsWith(
                    "net.sfelabs.core.knox.feature.api.ConfigurableStatePolicy"
                ) == true
            } -> true

            // Check if it's directly a PolicyState implementation
            (valueType.declaration as? KSClassDeclaration)?.superTypes?.any { superType ->
                superType.resolve().declaration.qualifiedName?.asString() ==
                        PolicyState::class.qualifiedName
            } == true -> true

            // Check type parameters for PolicyState bound
            valueType.declaration.typeParameters.any { typeParam ->
                typeParam.bounds.any { bound ->
                    bound.resolve().declaration.qualifiedName?.asString() ==
                            PolicyState::class.qualifiedName
                }
            } -> true

            else -> false
        }



        if (!hasPolicyStateInterface) {
            environment.logger.error(
                "Policy state type ${valueType.declaration.qualifiedName?.asString()} must implement PolicyState",
                valueType.declaration
            )
            return null
        }

        return ProcessedPolicy(
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
                PolicyCategory.valueOf(it.declaration.simpleName.asString())
            } ?: return null,
            valueType = valueType,
            configType = configType,
            declaration = classDeclaration
        )
    }
}