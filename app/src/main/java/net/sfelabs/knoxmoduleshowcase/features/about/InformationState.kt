package net.sfelabs.knoxmoduleshowcase.features.about

data class InformationState(
    val isLoaded: Boolean = false,
    val gen2SoftwareList: List<TacticalEditionSoftware> = emptyList(),
    val gen2ExtensionSoftwareList: List<TacticalEditionSoftware> = emptyList(),
    val gen3SoftwareList: List<TacticalEditionGen3> = emptyList()
)
