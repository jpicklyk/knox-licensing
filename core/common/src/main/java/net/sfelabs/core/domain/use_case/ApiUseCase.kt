package net.sfelabs.core.domain.use_case

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import net.sfelabs.core.di.IoDispatcher
import net.sfelabs.core.domain.UiText
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.api.DefaultApiError

/**
 * Represents a use case for API operations.
 *
 * @param P The type of input parameters for the use case. Use [Unit] if no parameters are required.
 * @param R The type of the result returned by the use case.
 */
interface ApiUseCase<in P, out R : Any> {
    /**
     * Executes the use case.
     *
     * @param params The input parameters for the use case. Can be null if no parameters are required.
     * @return An [ApiResult] representing the result of the operation.
     */
    suspend operator fun invoke(params: P? = null): ApiResult<R>
}

/**
 * An abstract implementation of [ApiUseCase] that provides coroutine context switching and error handling.
 *
 * @param P The type of input parameters for the use case. Use [Unit] if no parameters are required.
 * @param R The type of the result returned by the use case.
 * @param defaultDispatcher The default coroutine dispatcher to use for execution.
 * @param dispatcher The coroutine dispatcher to use for this specific use case. Defaults to defaultDispatcher.
 */
abstract class CoroutineApiUseCase<in P, out R : Any>(
    @IoDispatcher defaultDispatcher: CoroutineDispatcher,
    private val dispatcher: CoroutineDispatcher = defaultDispatcher
) : ApiUseCase<P, R> {

    /**
     * Executes the use case with error handling and context switching.
     *
     * @param params The input parameters for the use case. Can be null if no parameters are required.
     * @return An [ApiResult] representing the result of the operation.
     */
    override suspend operator fun invoke(params: P?): ApiResult<R> = withContext(dispatcher) {
        try {
            execute(params)
        } catch (e: Throwable) {
            mapError(e)
        }
    }

    /**
     * Implements the core logic of the use case.
     * This method should be implemented by subclasses to define the specific behavior of the use case.
     *
     * @param params The input parameters for the use case. Can be null if no parameters are required.
     * @return An [ApiResult] representing the result of the operation.
     */
    protected abstract suspend fun execute(params: P?): ApiResult<R>

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
            UiText.DynamicString("Permission error: ${throwable.message}"),
            apiError = DefaultApiError.PermissionError("Permission error: ${throwable.message}"),
            exception = throwable
        )
        else -> ApiResult.Error(
            UiText.DynamicString("An unexpected error occurred"),
            apiError = DefaultApiError.UnexpectedError,
            exception = Exception(throwable)
        )
    }
}