package net.sfelabs.core.domain.usecase.base

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.model.DefaultApiError

/**
 * An abstract implementation of [UseCase] that provides coroutine context switching and error handling.
 *
 * @param P The type of input parameters for the use case. Use [Unit] if no parameters are required.
 * @param R The type of the result returned by the use case.
 * @param dispatcher The coroutine dispatcher to use for this specific use case. Defaults to an [IoDispatcher].
 */
abstract class SuspendingUseCase<in P, out R : Any>(
    private val dispatcher: CoroutineDispatcher? = null
) : UseCase<P, R> {
    private val defaultDispatcher = Dispatchers.IO

    /**
     * Executes the use case with error handling and context switching.
     *
     * @param params The input parameters for the use case.
     * @return An [ApiResult] representing the result of the operation.
     */
    final override suspend operator fun invoke(params: P): ApiResult<R> =
        withContext(
            dispatcher ?: defaultDispatcher
        ) {
            try {
                execute(params)
            } catch (e: Throwable) {
                currentCoroutineContext().ensureActive()
                mapError(e)
            }
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