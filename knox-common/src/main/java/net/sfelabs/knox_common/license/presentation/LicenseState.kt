package net.sfelabs.knox_common.license.presentation

sealed class LicenseState {
    data object Loading : LicenseState()
    data object NotActivated : LicenseState()
    data class Activated(val message: String) : LicenseState()
    data object Expired : LicenseState()
    data object Terminated : LicenseState()
    data class Error(val message: String) : LicenseState()

    fun isActivated() = this is Activated
    fun isNotActivated() = this !is Activated
    fun isExpired() = this is Expired
    fun isTerminated() = this is Terminated
    fun isLoading() = this is Loading
    fun getErrorOrNull() = (this as? Error)?.message
    fun getName(): String = this.javaClass.simpleName
}

