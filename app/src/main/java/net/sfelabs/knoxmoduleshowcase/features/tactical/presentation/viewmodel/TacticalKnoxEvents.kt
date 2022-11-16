package net.sfelabs.knoxmoduleshowcase.features.tactical.presentation.viewmodel

sealed class TacticalKnoxEvents {
    object GetTacticalDeviceMode: TacticalKnoxEvents()
    data class SetTacticalDeviceMode(val enable: Boolean): TacticalKnoxEvents()

    object GetAutoTouchSensitivity: TacticalKnoxEvents()
    data class SetAutoTouchSensitivity(val enable: Boolean): TacticalKnoxEvents()
}
