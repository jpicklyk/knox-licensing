package net.sfelabs.core.knoxfeature.domain.usecase.builder

import kotlinx.coroutines.CoroutineDispatcher
import net.sfelabs.core.knoxfeature.domain.usecase.base.FeatureUseCase

abstract class FeatureUseCaseBuilder<T, P, R : Any> {
    protected var api: Any? = null
    protected var dispatcher: CoroutineDispatcher? = null

    fun withApi(api: Any): FeatureUseCaseBuilder<T, P, R> {
        this.api = api
        return this
    }

    fun withDispatcher(dispatcher: CoroutineDispatcher): FeatureUseCaseBuilder<T, P, R> {
        this.dispatcher = dispatcher
        return this
    }

    abstract fun build(): FeatureUseCase<T, P, R>
}