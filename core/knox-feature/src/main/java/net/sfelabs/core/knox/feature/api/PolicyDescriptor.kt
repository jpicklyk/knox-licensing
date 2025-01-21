package net.sfelabs.core.knox.feature.api

interface PolicyDescriptor<T: PolicyState> {
    val key: FeatureKey<T>
    val component: FeatureComponent<T>
}