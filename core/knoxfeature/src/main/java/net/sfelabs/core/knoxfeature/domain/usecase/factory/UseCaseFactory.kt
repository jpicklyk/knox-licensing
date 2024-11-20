package net.sfelabs.core.knoxfeature.domain.usecase.factory

import net.sfelabs.core.domain.use_case.ApiUseCase

object UseCaseFactory {
    private val useCases = mutableMapOf<String, ApiUseCase<*, *>>()

    fun <P, R : Any> register(
        name: String,
        useCase: ApiUseCase<P, R>
    ) {
        useCases[name] = useCase
    }

    @Suppress("UNCHECKED_CAST")
    fun <P, R : Any> getUseCase(name: String): ApiUseCase<P, R> {
        return useCases[name] as? ApiUseCase<P, R>
            ?: throw IllegalArgumentException("No use case found: $name")
    }

    fun clearUseCases() {
        useCases.clear()
    }
}