package net.sfelabs.core.domain

interface KnoxApiEnabled {
    suspend fun isApiEnabled(): ApiCall<Boolean>
}