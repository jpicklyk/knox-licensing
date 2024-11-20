package net.sfelabs.core.knoxfeature.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import net.sfelabs.core.knoxfeature.annotation.GeneratedFeatureUseCase
import net.sfelabs.core.knoxfeature.annotation.GeneratedUseCase

class UseCaseProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        // Now we can access generateUseCase
        val regularUseCaseProcessor = GeneratedUseCaseProcessor(codeGenerator, logger)
        resolver.getSymbolsWithAnnotation(GeneratedUseCase::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .forEach { regularUseCaseProcessor.generateUseCase(it) }

        val featureUseCaseProcessor = GeneratedFeatureUseCaseProcessor(codeGenerator, logger)
        resolver.getSymbolsWithAnnotation(GeneratedFeatureUseCase::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .forEach { featureUseCaseProcessor.generateFeatureUseCase(it) }

        return emptyList()
    }
}

