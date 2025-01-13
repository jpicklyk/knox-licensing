package net.sfelabs.core.domain.usecase.base

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.model.DefaultApiError

/**
 * A base implementation of [UseCase] that provides error mapping.
 *
 * @param P The type of input parameters for the use case. Use [Unit] if no parameters are required.
 * @param R The type of the result returned by the use case.
 */
abstract class BaseUseCase<in P, out R : Any> : UseCase<P, R> {
    /**
     * Executes the use case with error handling.
     *
     * @param params The input parameters for the use case.
     * @return An [ApiResult] representing the result of the operation.
     */
    final override suspend operator fun invoke(params: P): ApiResult<R> =
        try {
            execute(params)
        } catch (e: Throwable) {
            mapError(e)
        }

    /**
     * Implements the core logic of the use case.
     * This method should be implemented by subclasses to define the specific behavior of the use case.
     *
     * @param params The input parameters for the use case.
     * @return An [ApiResult] representing the result of the operation.
     */
    @Suppress("UNCHECKED_CAST")
    protected abstract suspend fun execute(params: P = Unit as P): ApiResult<R>

    /**
     * Maps exceptions to appropriate [ApiResult.Error] instances.
     * This method can be overridden in subclasses to provide custom error mapping.
     *
     * @param throwable The throwable to be mapped.
     * @return An [ApiResult] representing the error state.
     */
    protected open fun mapError(throwable: Throwable): ApiResult<R> = when (throwable) {
        is NoSuchMethodError -> ApiResult.NotSupported
        is SecurityException -> ApiResult.Error(
            apiError = DefaultApiError.PermissionError("Permission error: ${throwable.message}"),
            exception = throwable
        )
        else -> ApiResult.Error(
            apiError = DefaultApiError.UnexpectedError(),
            exception = Exception(throwable)
        )
    }
}