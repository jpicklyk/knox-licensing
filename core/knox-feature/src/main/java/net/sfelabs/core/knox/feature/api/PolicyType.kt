package net.sfelabs.core.knox.feature.api

interface PolicyType<T: PolicyState> {
    val key: FeatureKey<T>
    val component: FeatureComponent<T>
}