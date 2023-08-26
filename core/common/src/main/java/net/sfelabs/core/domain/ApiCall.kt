package net.sfelabs.core.domain

sealed class ApiCall<out T : Any> {
    data class Success<out T : Any>(val data: T, val uiText: UiText? = null): ApiCall<T>()
    data class Error(val uiText: UiText): ApiCall<Nothing>()
    object NotSupported: ApiCall<Nothing>()
}