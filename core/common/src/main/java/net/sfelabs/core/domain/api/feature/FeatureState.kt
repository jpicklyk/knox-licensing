package net.sfelabs.core.domain.api.feature

data class FeatureState<out T>(val enabled: Boolean, val value: T)
