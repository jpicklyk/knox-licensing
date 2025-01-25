package net.sfelabs.knox_tactical.domain.policy.hdm

import net.sfelabs.core.knox.feature.api.PolicyConfiguration
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.core.knox.feature.ui.model.ConfigurationOption

data class HdmConfiguration(
    //Not implementing the mapEnabled capability in this policy configuration.
    override val stateMapping: StateMapping = StateMapping.DIRECT
) : PolicyConfiguration<HdmState, Int> {

    // mapEnabled would need to be handled here if implemented
    override fun fromApiData(apiData: Int): HdmState {
        return HdmState(
            isEnabled = apiData != 0,
            policyMask = apiData
        )
    }

    // mapEnabled would need to be handled here if implemented
    override fun toApiData(state: HdmState): Int {
        return state.policyMask
    }

    override fun fromUiState(
        uiEnabled: Boolean,
        options: List<ConfigurationOption>
    ): HdmState {
        val mask = if(uiEnabled) {
            val selectedComponents = options.filterIsInstance<ConfigurationOption.Toggle>()
                .filter { it.isEnabled }
                .mapNotNull { option ->
                    HdmComponent.entries.find { it.name == option.key }
                }
                .toSet()

            selectedComponents.fold(0) { acc, component ->
                acc or component.mask
            }
        } else {
            0 // If policy is disabled, the mask should be 0
        }

        return HdmState(
            isEnabled = uiEnabled,
            policyMask = mask
        )
    }

    override fun getConfigurationOptions(state: HdmState): List<ConfigurationOption> =
        HdmComponent.entries.map { component ->
            ConfigurationOption.Toggle(
                key = component.name,
                label = component.displayName,
                isEnabled = component.mask and state.policyMask != 0
            )
    }


//    private fun invertBitmask(mask: Int, width: Int): Int {
//        return mask.inv() and ((1 shl width) - 1)
//    }
}