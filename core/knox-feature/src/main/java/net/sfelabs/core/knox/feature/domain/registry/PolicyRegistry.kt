package net.sfelabs.core.knox.feature.domain.registry

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.domain.model.Policy
import net.sfelabs.core.knox.feature.api.PolicyCategory
import net.sfelabs.core.knox.feature.domain.usecase.handler.PolicyHandler
import net.sfelabs.core.knox.feature.api.PolicyComponent
import net.sfelabs.core.knox.feature.api.PolicyKey
import net.sfelabs.core.knox.feature.api.PolicyState

interface PolicyRegistry {
    fun <T : PolicyState> getHandler(key: PolicyKey<T>): PolicyHandler<T>?
    suspend fun getAllPolicies(): List<Policy<PolicyState>>
    suspend fun getPolicies(category: PolicyCategory): List<Policy<PolicyState>>
    fun isRegistered(key: PolicyKey<*>): Boolean
    fun getComponent(key: PolicyKey<*>): PolicyComponent<out PolicyState>?
    suspend fun getPolicyState(featureName: String): Policy<PolicyState>?
    suspend fun <T : PolicyState> setPolicyState(policyKey: PolicyKey<T>, state: T): ApiResult<Unit>
}