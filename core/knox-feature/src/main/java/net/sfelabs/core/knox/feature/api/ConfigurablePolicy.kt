package net.sfelabs.core.knox.feature.api

interface ConfigurablePolicy<T: PolicyState, C: PolicyConfiguration<T>> {
    fun toConfiguration(state: T): C
}