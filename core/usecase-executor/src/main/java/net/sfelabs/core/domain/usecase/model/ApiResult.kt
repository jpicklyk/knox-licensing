package net.sfelabs.core.domain.usecase.model

sealed class ApiResult<out T : Any> {
    data class Success<out T : Any>(val data: T): ApiResult<T>()
    data class Error(
        //TODO - Refactor code to get rid of UiText and replace with mandatory ApiError
        //val uiText: UiText,
        val apiError: ApiError = DefaultApiError.UnexpectedError(),
        val exception: Exception? = null
    ): ApiResult<Nothing>()
    data object NotSupported: ApiResult<Nothing>()

    // Helper function to easily get the data or null
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    fun getErrorOrNull(): ApiError? = when (this) {
        is Error -> apiError
        else -> null
    }

    fun getExceptionOrNull(): Exception? = when (this) {
        is Error -> exception
        else -> null
    }
}

/**
 * Transforms the success value of an ApiResult while preserving its result state.
 *
 * When the ApiResult is:
 * - Success: Applies the transform function to the data and wraps it in a new Success
 * - Error: Returns the original error with its apiError and exception
 * - NotSupported: Returns NotSupported unchanged
 *
 * @param T The type of the original success value
 * @param R The type of the transformed success value
 * @param transform A function to transform the success value from type T to type R
 * @return A new ApiResult with the transformed success value or the original error state
 */
fun <T : Any, R : Any> ApiResult<T>.map(transform: (T) -> R): ApiResult<R> {
    return when (this) {
        is ApiResult.Success -> ApiResult.Success(transform(data))
        is ApiResult.Error -> ApiResult.Error(apiError, exception)
        ApiResult.NotSupported -> ApiResult.NotSupported
    }
}