package net.sfelabs.core.domain

import net.sfelabs.core.domain.api.ApiResult

interface KnoxApiEnabled {
    suspend fun isApiEnabled(): ApiResult<Boolean>
}