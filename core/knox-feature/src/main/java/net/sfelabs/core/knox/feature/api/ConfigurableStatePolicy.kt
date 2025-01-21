package net.sfelabs.core.knox.feature.api

// Base class for complex policies
abstract class ConfigurableStatePolicy<T : PolicyState, C : PolicyConfiguration<T>>(
    protected val stateMapping: StateMapping = StateMapping.DIRECT
) : FeatureContract<T>, ConfigurablePolicy<T, C> {

    protected fun mapEnabled(enabled: Boolean): Boolean = when (stateMapping) {
        StateMapping.DIRECT -> enabled
        StateMapping.INVERTED -> !enabled
    }
}