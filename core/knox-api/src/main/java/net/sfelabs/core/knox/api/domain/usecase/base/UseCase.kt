package net.sfelabs.core.knox.api.domain.usecase.base

import net.sfelabs.core.knox.api.domain.model.ApiResult

/**
 * Represents a use case for API operations.
 *
 * @param P The type of input parameters for the use case. Use [Unit] if no parameters are required.
 * @param R The type of the result returned by the use case.
 */
interface UseCase<in P, out R : Any> {
    /**
     * Executes the use case.
     *
     * @param params The input parameters for the use case.
     * @return An [net.sfelabs.core.knox.api.domain.model.ApiResult] representing the result of the operation.
     */
    @Suppress("UNCHECKED_CAST")
    suspend operator fun invoke(params: P = Unit as P): ApiResult<R>
}