package net.sfelabs.knox_tactical.domain.policy.auto_call_pickup

import net.sfelabs.core.knox.feature.api.PolicyConfiguration
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.core.knox.feature.ui.model.ConfigurationOption
import net.sfelabs.knox_tactical.domain.model.AutoCallPickupMode

data class AutoCallPickupConfiguration(
    val mode: AutoCallPickupMode,
    override val stateMapping: StateMapping = StateMapping.DIRECT
): PolicyConfiguration<AutoCallPickupState> {
    override fun toState(currentState: AutoCallPickupState): AutoCallPickupState {
        // If the mode is Disable, ensure isEnabled is false
        return currentState.copy(
            isEnabled = mode != AutoCallPickupMode.Disable,
            mode = mode.takeUnless { it == AutoCallPickupMode.Disable }
                ?: AutoCallPickupMode.EnableAlwaysAccept
        )
    }

    override fun toConfigurationOptions(): List<ConfigurationOption> = listOf(
        ConfigurationOption.Choice(
            key = "mode",
            label = "Mode",
            options = AutoCallPickupMode.values.map { it.displayName },
            selected = mode.displayName
        )
    )

    override fun fromConfigurationOptions(
        options: List<ConfigurationOption>,
        enabled: Boolean
    ): AutoCallPickupState {
        val mode = options.filterIsInstance<ConfigurationOption.Choice>()
            .find { it.key == "mode" }
            ?.selected
            ?.let { AutoCallPickupMode.fromDisplayName(it) }
            ?: AutoCallPickupMode.Disable

        return AutoCallPickupState(
            isEnabled = mapEnabled(enabled),
            mode = mode
        )
    }
}
