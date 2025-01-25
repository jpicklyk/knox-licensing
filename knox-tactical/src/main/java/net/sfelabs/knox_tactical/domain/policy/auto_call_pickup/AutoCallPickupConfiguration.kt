package net.sfelabs.knox_tactical.domain.policy.auto_call_pickup

import net.sfelabs.core.knox.feature.api.PolicyConfiguration
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.core.knox.feature.ui.model.ConfigurationOption
import net.sfelabs.knox_tactical.domain.model.AutoCallPickupMode

data class AutoCallPickupConfiguration(
    override val stateMapping: StateMapping = StateMapping.DIRECT
): PolicyConfiguration<AutoCallPickupState, AutoCallPickupMode> {
    override fun fromApiData(apiData: AutoCallPickupMode): AutoCallPickupState {
        val modeOrDefault = when (apiData) {
            AutoCallPickupMode.Disable -> AutoCallPickupMode.EnableAlwaysAccept
            else -> apiData
        }
        return AutoCallPickupState(
            isEnabled = apiData != AutoCallPickupMode.Disable,
            mode = modeOrDefault
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
        val mode = options.filterIsInstance<ConfigurationOption.Choice>()
            .find { it.key == "mode" }
            ?.selected
            ?.let { AutoCallPickupMode.fromDisplayName(it) }
            ?: AutoCallPickupMode.Disable

        return AutoCallPickupState(
            isEnabled = mapEnabled(uiEnabled),
            mode = mode
        )
    }

    override fun getConfigurationOptions(state: AutoCallPickupState): List<ConfigurationOption> = listOf(
        ConfigurationOption.Choice(
            key = "mode",
            label = "Mode",
            options = listOf(
                AutoCallPickupMode.Enable.displayName,
                AutoCallPickupMode.EnableAlwaysAccept.displayName
            ),
            selected = state.mode.displayName
        )
    )

}
