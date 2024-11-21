package net.sfelabs.knox_tactical.domain.api

import net.sfelabs.core.knox.feature.domain.model.FeatureKey

sealed class TacticalRestrictions<T>(override val featureName: String) : FeatureKey<T> {
    data object TacticalDeviceMode : TacticalRestrictions<Boolean>("tactical_device_mode")
}