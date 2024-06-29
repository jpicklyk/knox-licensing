package net.sfelabs.core.domain.api

sealed interface ApiError {
    val message: String
}

sealed class DefaultApiError : ApiError {
    data class PermissionError(override val message: String) : DefaultApiError()
    data object UnexpectedError : DefaultApiError() {
        override val message: String = "An unexpected error occurred"
    }
}