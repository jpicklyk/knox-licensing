package net.sfelabs.core.knox.feature.api

interface PolicyDescriptor<T: PolicyState> {
    val key: PolicyKey<T>
    val component: PolicyComponent<T>
}