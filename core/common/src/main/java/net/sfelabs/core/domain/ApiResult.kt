package net.sfelabs.core.domain

data class ApiResult<T>(val enabled: Boolean, val apiValue: T)
