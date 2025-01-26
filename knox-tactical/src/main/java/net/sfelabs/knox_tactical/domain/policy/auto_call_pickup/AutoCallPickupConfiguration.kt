package net.sfelabs.knox_tactical.domain.policy.auto_call_pickup

import net.sfelabs.core.knox.feature.api.PolicyConfiguration
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.core.knox.feature.ui.model.ConfigurationOption
import net.sfelabs.knox_tactical.domain.model.AutoCallPickupMode

data class AutoCallPickupConfiguration(
    override val stateMapping: StateMapping = StateMapping.DIRECT
): PolicyConfiguration<AutoCallPickupState, AutoCallPickupMode> {
    override fun fromApiData(apiData: AutoCallPickupMode): AutoCallPickupState {

        return AutoCallPickupState(
            isEnabled = apiData != AutoCallPickupMode.Disable,
            mode = apiData
        )
    }

    override fun toApiData(state: AutoCallPickupState): AutoCallPickupMode {
        //There is no need to perform mapEnabled here, just act as a passthrough
        return state.mode
    }

    override fun fromUiState(
        uiEnabled: Boolean,
        options: List<ConfigurationOption>
    ): AutoCallPickupState {
        val mode = if (uiEnabled) {
            options.filterIsInstance<ConfigurationOption.Choice>()
                .find { it.key == "mode" }
                ?.selected
                ?.let { AutoCallPickupMode.fromDisplayName(it) }
                ?: AutoCallPickupMode.Disable
        } else {
            AutoCallPickupMode.Disable
        }

        return AutoCallPickupState(
            isEnabled = mapEnabled(uiEnabled),
            mode = mode
        )
    }

    override fun getConfigurationOptions(state: AutoCallPickupState): List<ConfigurationOption> = listOf(
        ConfigurationOption.Choice(
            key = "mode",
            label = "Mode",
            options = AutoCallPickupMode.values.map { it.displayName },
            selected = state.mode.displayName
        )
    )

}
