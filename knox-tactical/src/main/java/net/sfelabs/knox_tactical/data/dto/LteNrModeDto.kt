package net.sfelabs.knox_tactical.data.dto

import net.sfelabs.knox_tactical.domain.model.LteNrMode

data class LteNrModeDto(
    val simSlotId: Int? = null,
    val mode: LteNrMode = LteNrMode.EnableBothSaAndNsa
)