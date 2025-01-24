package net.sfelabs.knox_tactical.domain.policy.modem_ims

import net.sfelabs.core.knox.feature.api.PolicyConfiguration
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.core.knox.feature.ui.model.ConfigurationOption

data class ImsConfiguration(
    val simSlotId: Int,
    val enabled: Boolean,
    override val stateMapping: StateMapping
) : PolicyConfiguration<ImsState> {
    override fun toState(currentState: ImsState): ImsState {
        return currentState.copy(
            isEnabled = mapEnabled(enabled),
            simSlotId = simSlotId
        )
    }

    override fun toConfigurationOptions(): List<ConfigurationOption> = listOf(
        ConfigurationOption.NumberInput(
            key = "simSlotId",
            label = "SIM Slot Id",
            value = simSlotId,
            range = 0..2
        )
    )

    override fun fromConfigurationOptions(
        options: List<ConfigurationOption>,
        enabled: Boolean
    ): ImsState {
        val simSlotId = options.filterIsInstance<ConfigurationOption.NumberInput>()
            .find { it.key == "simSlotId" }
            ?.value
            ?: 0
        return ImsState(
            isEnabled = mapEnabled(enabled),
            simSlotId = simSlotId
        )

    }
}