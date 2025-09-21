package com.github.jpicklyk.knox.licensing.domain

sealed class LicenseResult {
    data class Success(val message: String) : LicenseResult()
    data class Error(val message: String, val errorCode: Int? = null) : LicenseResult()
}

fun LicenseResult.isSuccess(): Boolean = this is LicenseResult.Success
fun LicenseResult.isError(): Boolean = this is LicenseResult.Error