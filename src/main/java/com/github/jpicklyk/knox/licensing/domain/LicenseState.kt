package com.github.jpicklyk.knox.licensing.domain

sealed class LicenseState {
    object Loading : LicenseState()
    data class Activated(val message: String) : LicenseState()
    data class Deactivated(val message: String) : LicenseState()
    data class Error(val message: String) : LicenseState()
}