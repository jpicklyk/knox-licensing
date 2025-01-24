package net.sfelabs.knox_tactical.domain.policy.hdm

import net.sfelabs.core.knox.feature.api.PolicyConfiguration
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.core.knox.feature.ui.model.ConfigurationOption

data class HdmConfiguration(
    val components: Set<HdmComponent>,
    val policyMask: Int,
    override val stateMapping: StateMapping = StateMapping.DIRECT
) : PolicyConfiguration<HdmState> {
    override fun toState(currentState: HdmState): HdmState {
        val newMask = components.fold(0) { acc, component ->
            acc or component.mask
        }
        return currentState.copy(policyMask = newMask)
    }

    override fun toConfigurationOptions(): List<ConfigurationOption> =
        HdmComponent.entries.map { component ->
            ConfigurationOption.Toggle(
                key = component.name,
                label = component.displayName,
                 isEnabled = policyMask and component.mask != 0
            )
        }

    override fun fromConfigurationOptions(
        options: List<ConfigurationOption>,
        enabled: Boolean
    ): HdmState {
        val selectedComponents = options.filterIsInstance<ConfigurationOption.Toggle>()
                .filter { it.isEnabled }
            .mapNotNull { option ->
                HdmComponent.entries.find { it.name == option.key }
            }
            .toSet()

        val mask = selectedComponents.fold(0) { acc, component ->
            acc or component.mask
        }

        return HdmState(
            isEnabled = mapEnabled(enabled && mask != 0) ,
            policyMask = mask
        )
    }
}