package com.github.jpicklyk.knox.licensing.domain

data class LicenseInfo(
    val isActivated: Boolean,
    val licenseKey: String? = null,
    val activationDate: String? = null,
    val expirationDate: String? = null,
    val errorCode: Int? = null,
    val errorMessage: String? = null
)