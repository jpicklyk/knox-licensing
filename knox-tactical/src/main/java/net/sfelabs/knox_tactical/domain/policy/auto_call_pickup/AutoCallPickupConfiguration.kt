package net.sfelabs.knox_tactical.domain.policy.auto_call_pickup

import net.sfelabs.core.knox.feature.api.PolicyConfiguration
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.core.knox.feature.ui.model.ConfigurationOption
import net.sfelabs.knox_tactical.data.dto.AutoCallPickupDto
import net.sfelabs.knox_tactical.domain.model.AutoCallPickupMode

data class AutoCallPickupConfiguration(
    override val stateMapping: StateMapping = StateMapping.DIRECT
): PolicyConfiguration<AutoCallPickupState, AutoCallPickupDto> {
    override fun fromApiData(apiData: AutoCallPickupDto): AutoCallPickupState {

        return AutoCallPickupState(
            isEnabled = apiData.mode != AutoCallPickupMode.Disable,
            mode = apiData.mode
        )
    }

    override fun toApiData(state: AutoCallPickupState): AutoCallPickupDto {
        //There is no need to perform mapEnabled here, just act as a passthrough
        return AutoCallPickupDto(mode =state.mode)
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
