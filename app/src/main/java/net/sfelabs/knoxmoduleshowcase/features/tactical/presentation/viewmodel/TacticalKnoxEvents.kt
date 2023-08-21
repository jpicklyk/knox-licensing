package net.sfelabs.knoxmoduleshowcase.features.tactical.presentation.viewmodel

import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
sealed class TacticalKnoxEvents {
    data class FeatureChanged(val featureName: String, val isEnabled: Boolean): TacticalKnoxEvents()
    object GetTacticalDeviceMode: TacticalKnoxEvents()
    data class SetTacticalDeviceMode(val enable: Boolean): TacticalKnoxEvents()

    object GetAutoTouchSensitivity: TacticalKnoxEvents()
    data class SetAutoTouchSensitivity(val enable: Boolean): TacticalKnoxEvents()
}
