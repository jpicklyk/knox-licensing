package net.sfelabs.knox_tactical.domain.policy.nr_mode

import net.sfelabs.core.knox.feature.api.PolicyConfiguration
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.core.knox.feature.ui.model.ConfigurationOption
import net.sfelabs.knox_tactical.domain.model.LteNrMode

data class NrModeConfiguration(
    val mode: LteNrMode,
    val simSlotId: Int? = null,
    override val stateMapping: StateMapping = StateMapping.DIRECT
) : PolicyConfiguration<NrModeState> {
    override fun toState(currentState: NrModeState): NrModeState {
        return currentState.copy(
            isEnabled = mode != LteNrMode.EnableBothSaAndNsa,
            mode = mode.takeUnless { it == LteNrMode.EnableBothSaAndNsa }
                ?: LteNrMode.DisableNsa,
            simSlotId = simSlotId
        )
    }

    override fun toConfigurationOptions(): List<ConfigurationOption> = listOf(
        ConfigurationOption.NumberInput(
            key = "simSlotId",
            label = "SIM Slot Id",
            value = simSlotId ?: 0,
            range = 0..2
        ),
        ConfigurationOption.Choice(
            key = "mode",
            label = "Mode",
            options = LteNrMode.values.map { it.displayName },
            selected = mode.displayName
        )
    )

    override fun fromConfigurationOptions(
        options: List<ConfigurationOption>,
        enabled: Boolean
    ): NrModeState {
        val simSlotId = options.filterIsInstance<ConfigurationOption.NumberInput>()
            .find { it.key == "simSlotId" }
            ?.value ?: 0
        val modeDisplayName = options.filterIsInstance<ConfigurationOption.Choice>()
            .find { it.key == "mode" }
            ?.selected ?: LteNrMode.EnableBothSaAndNsa.displayName

        return NrModeState(
            isEnabled = mapEnabled(enabled),
            mode = LteNrMode.fromDisplayName(modeDisplayName),
            simSlotId = simSlotId
        )
    }

    companion object {
        fun disabled(simSlotId: Int? = null) = NrModeConfiguration(
            mode = LteNrMode.EnableBothSaAndNsa,
            simSlotId = simSlotId
        )
    }
}