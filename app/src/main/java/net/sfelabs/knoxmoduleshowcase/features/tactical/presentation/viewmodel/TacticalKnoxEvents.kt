package net.sfelabs.knoxmoduleshowcase.features.tactical.presentation.viewmodel

import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
sealed class TacticalKnoxEvents {
    data class FeatureOnOffChanged(val featureKey: String, val isEnabled: Boolean, val data: Any? = null): TacticalKnoxEvents()
}
