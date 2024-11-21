package net.sfelabs.core.knox.api.domain

sealed interface ApiError {
    val message: String
}

sealed class DefaultApiError : ApiError {
    data class PermissionError(override val message: String) : DefaultApiError()
    data class TimeoutError(override val message: String) : DefaultApiError()
    data class UnexpectedError(
        override val message: String = "An unexpected error occurred"
    ) : DefaultApiError()
}