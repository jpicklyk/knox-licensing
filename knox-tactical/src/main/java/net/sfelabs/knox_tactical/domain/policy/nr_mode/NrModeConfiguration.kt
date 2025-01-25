package net.sfelabs.knox_tactical.domain.policy.nr_mode

import net.sfelabs.core.knox.feature.api.PolicyConfiguration
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.core.knox.feature.ui.model.ConfigurationOption
import net.sfelabs.knox_tactical.domain.model.LteNrMode

data class NrModeConfiguration(
    override val stateMapping: StateMapping = StateMapping.DIRECT
) : PolicyConfiguration<NrModeState, LteNrMode> {

    override fun fromApiData(apiData: LteNrMode): NrModeState {
        //TODO: Need a new data class to wrap both SIM slot id and LteNrMode
        return NrModeState (
            isEnabled = ( apiData != LteNrMode.EnableBothSaAndNsa ),
            mode = apiData.takeUnless { it == LteNrMode.EnableBothSaAndNsa }
                ?: LteNrMode.DisableNsa,
            simSlotId = 0 // We need this fixed
        )
    }

    override fun toApiData(state: NrModeState): LteNrMode {
        return when(state.isEnabled) {
            true -> state.mode
            false -> LteNrMode.EnableBothSaAndNsa
        }
    }

    override fun fromUiState(
        uiEnabled: Boolean,
        options: List<ConfigurationOption>
    ): NrModeState {
        val simSlotId = options.filterIsInstance<ConfigurationOption.NumberInput>()
            .find { it.key == "simSlotId" }
            ?.value ?: 0
        val modeDisplayName = options.filterIsInstance<ConfigurationOption.Choice>()
            .find { it.key == "mode" }
            ?.selected ?: LteNrMode.EnableBothSaAndNsa.displayName

        return NrModeState(
            isEnabled = uiEnabled,
            mode = LteNrMode.fromDisplayName(modeDisplayName),
            simSlotId = simSlotId
        )
    }

    override fun getConfigurationOptions(state: NrModeState): List<ConfigurationOption> = listOf(
        ConfigurationOption.NumberInput(
            key = "simSlotId",
            label = "SIM Slot Id",
            value = state.simSlotId ?: 0,
            range = 0..2
        ),
        ConfigurationOption.Choice(
            key = "mode",
            label = "Mode",
            options = listOf(LteNrMode.DisableNsa.displayName, LteNrMode.DisableSa.displayName),
            selected = state.mode.displayName
        )
    )

}