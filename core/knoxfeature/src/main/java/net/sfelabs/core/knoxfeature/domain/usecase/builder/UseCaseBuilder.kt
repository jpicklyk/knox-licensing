package net.sfelabs.core.knoxfeature.domain.usecase.builder

import kotlinx.coroutines.CoroutineDispatcher
import net.sfelabs.core.domain.use_case.ApiUseCase

abstract class UseCaseBuilder<P, R : Any> {
    protected var api: Any? = null
    protected var dispatcher: CoroutineDispatcher? = null

    fun withApi(api: Any): UseCaseBuilder<P, R> {
        this.api = api
        return this
    }

    fun withDispatcher(dispatcher: CoroutineDispatcher): UseCaseBuilder<P, R> {
        this.dispatcher = dispatcher
        return this
    }

    abstract fun build(): ApiUseCase<P, R>
}