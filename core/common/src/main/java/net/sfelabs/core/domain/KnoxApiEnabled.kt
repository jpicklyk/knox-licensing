package net.sfelabs.core.domain

import net.sfelabs.core.knox.api.domain.model.ApiResult

interface KnoxApiEnabled {
    suspend fun isApiEnabled(): ApiResult<Boolean>
}