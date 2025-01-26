package net.sfelabs.core.knox.feature.api

import net.sfelabs.core.knox.feature.domain.usecase.handler.PolicyHandler

interface PolicyComponent<T : PolicyState> {
    val policyName: String
    val title: String
    val description: String
    val category: PolicyCategory
    val handler: PolicyHandler<T>
    val defaultValue: T
    val key: PolicyKey<T>

}