package net.sfelabs.knox_tactical.domain.model

data class ImsState(
    val isEnabled: Boolean,
    val simSlotId: Int = 0,
    val feature: Int = 1
)
