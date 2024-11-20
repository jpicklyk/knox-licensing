package net.sfelabs.core.knoxfeature.domain.usecase.base

import kotlinx.coroutines.CoroutineDispatcher
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.use_case.CoroutineApiUseCase
import net.sfelabs.core.knoxfeature.domain.metrics.UseCaseMetrics
import net.sfelabs.core.knoxfeature.domain.model.FeatureState

abstract class CoroutineFeatureUseCase<T, P, R : Any>(
    dispatcher: CoroutineDispatcher? = null
) : CoroutineApiUseCase<P, R>(dispatcher), FeatureUseCase<T, P, R> {

    private val metrics = UseCaseMetrics()

    final override suspend fun execute(params: P): ApiResult<R> {
        val startTime = System.currentTimeMillis()
        try {
            return when (val state = getState()) {
                is ApiResult.Success -> {
                    val result = if (state.data.enabled) {
                        executeEnabled(params, state.data)
                    } else {
                        executeDisabled(params, state.data)
                    }
                    metrics.recordSuccess(this::class.simpleName ?: "Unknown")
                    result
                }
                is ApiResult.Error -> state
                is ApiResult.NotSupported -> ApiResult.NotSupported
            }
        } finally {
            metrics.recordDuration(
                this::class.simpleName ?: "Unknown",
                System.currentTimeMillis() - startTime
            )
        }
    }

    protected abstract suspend fun executeEnabled(params: P, state: FeatureState<T>): ApiResult<R>
    protected abstract suspend fun executeDisabled(params: P, state: FeatureState<T>): ApiResult<R>
}