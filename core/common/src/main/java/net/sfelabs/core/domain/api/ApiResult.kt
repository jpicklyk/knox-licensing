package net.sfelabs.core.domain.api

import net.sfelabs.core.domain.UiText

sealed class ApiResult<out T : Any> {
    data class Success<out T : Any>(val data: T): ApiResult<T>()
    data class Error(
        //TODO - Refactor code to get rid of UiText and replace with mandatory ApiError
        val uiText: UiText,
        val apiError: ApiError = DefaultApiError.UnexpectedError,
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