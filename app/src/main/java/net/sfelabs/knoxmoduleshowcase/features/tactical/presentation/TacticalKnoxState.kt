package net.sfelabs.knoxmoduleshowcase.features.tactical.presentation

import net.sfelabs.knox.core.common.UiText


data class TacticalKnoxState(
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val errorText: UiText = UiText.DynamicString(
        ""
    ),
    val isTacticalDeviceModeEnabled: Boolean = false,
    val isAutoTouchSensitivityEnabled: Boolean = false
)
