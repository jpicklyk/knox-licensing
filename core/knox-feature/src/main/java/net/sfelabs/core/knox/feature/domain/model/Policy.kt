package net.sfelabs.core.knox.feature.domain.model

import net.sfelabs.core.knox.feature.api.PolicyKey
import net.sfelabs.core.knox.feature.api.PolicyState
import net.sfelabs.core.knox.feature.api.PolicyStateWrapper

data class Policy<out T : PolicyState> (
    val key: PolicyKey<T>,
    val state: PolicyStateWrapper<T>
)