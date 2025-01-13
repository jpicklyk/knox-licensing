package net.sfelabs.core.domain.usecase.executor

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.model.DefaultApiError

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

@Suppress("UNCHECKED_CAST")
inline fun <reified T, R : Any> List<ApiResult<*>>.combine(transform: (List<T>) -> R): ApiResult<R> {
    if (any { it !is ApiResult.Success<*> }) {
        return firstOrNull { it is ApiResult.Error } as? ApiResult.Error
            ?: ApiResult.Error(DefaultApiError.UnexpectedError())
    }

    val results = map { (it as ApiResult.Success<*>).data }
    if (results.any { it !is T }) {
        return ApiResult.Error(DefaultApiError.UnexpectedError())
    }

    return ApiResult.Success(transform(results as List<T>))
}