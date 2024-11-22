package net.sfelabs.core.knox.feature.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import net.sfelabs.core.knox.feature.annotation.FeatureUseCase
import net.sfelabs.core.knox.feature.domain.model.FeatureCategory
import net.sfelabs.core.knox.feature.processor.generator.HandlerGenerator
import net.sfelabs.core.knox.feature.processor.generator.KeyGenerator
import net.sfelabs.core.knox.feature.processor.generator.RegistrationGenerator
import net.sfelabs.core.knox.feature.processor.model.FeatureGroup

class FeatureProcessor(
    environment: SymbolProcessorEnvironment
) : SymbolProcessor {
    private val features = mutableMapOf<String, FeatureGroup>()
    private val keyGenerator = KeyGenerator(environment)
    private val handlerGenerator = HandlerGenerator(environment)
    private val registrationGenerator = RegistrationGenerator(environment)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(FeatureUseCase::class.qualifiedName!!)

        symbols.forEach { symbol ->
            val annotation = symbol.annotations.first {
                it.shortName.asString() == "FeatureUseCase"
            }

            val featureName = annotation.arguments[0].value as String
            val type = (annotation.arguments.find { it.name?.asString() == "type" }?.value as KSType)
                .declaration.simpleName.asString().let { enumValue ->
                    FeatureUseCase.Type.valueOf(enumValue)
                }

            val category = (annotation.arguments.find { it.name?.asString() == "category" }?.value as KSType)
                .declaration.simpleName.asString().let { enumValue ->
                    FeatureCategory.valueOf(enumValue)
                }

            val config = annotation.arguments.find { it.name?.asString() == "config" }?.value as KSType

            features.getOrPut(featureName) {
                FeatureGroup(featureName, category, config)
            }.apply {
                when (type) {
                    FeatureUseCase.Type.GETTER -> getter = symbol
                    FeatureUseCase.Type.SETTER -> setter = symbol
                }
            }
        }

        features.values
            .filter { it.getter != null && it.setter != null }
            .forEach { feature ->
                keyGenerator.generate(feature)
                handlerGenerator.generate(feature)
                registrationGenerator.generate(feature)
            }

        return emptyList()
    }
}