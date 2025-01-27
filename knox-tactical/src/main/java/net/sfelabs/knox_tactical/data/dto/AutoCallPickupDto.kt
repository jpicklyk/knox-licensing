package net.sfelabs.knox_tactical.data.dto

import net.sfelabs.knox_tactical.domain.model.AutoCallPickupMode

data class AutoCallPickupDto(
    val mode: AutoCallPickupMode = AutoCallPickupMode.Disable
)
