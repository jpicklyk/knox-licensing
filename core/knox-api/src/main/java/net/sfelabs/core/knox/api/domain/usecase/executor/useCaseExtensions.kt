package net.sfelabs.core.knox.api.domain.usecase.executor

import net.sfelabs.core.knox.api.domain.model.ApiResult

/**
 * Extension functions for UseCaseBuilder result validation
 */
fun List<ApiResult<*>>.assertAllSuccessful(): Boolean {
    if (isEmpty()) return false
    return all { it is ApiResult.Success }
}

fun List<ApiResult<*>>.assertAnySuccessful(): Boolean {
    if (isEmpty()) return false
    return any { it is ApiResult.Success }
}

fun List<ApiResult<*>>.assertNoneSuccessful(): Boolean {
    if (isEmpty()) return false
    return none { it is ApiResult.Success }
}

fun List<ApiResult<*>>.assertAllFailed(): Boolean {
    if (isEmpty()) return false
    return all { it is ApiResult.Error }
}

fun List<ApiResult<*>>.assertAnyFailed(): Boolean {
    if (isEmpty()) return false
    return any { it is ApiResult.Error }
}

fun List<ApiResult<*>>.assertNotSupported(): Boolean {
    if (isEmpty()) return false
    return any { it is ApiResult.NotSupported }
}