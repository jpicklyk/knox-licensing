package net.sfelabs.knox_tactical.domain.model

sealed class AutoCallPickupMode(val value: Int) {
    companion object {
        operator fun invoke(type: Int): AutoCallPickupMode {
            return when(type) {
                0 -> Disable
                1 -> Enable
                else -> EnableAlwaysAccept
            }
        }
    }
    data object Disable: AutoCallPickupMode(0)
    data object Enable: AutoCallPickupMode(1)
    /* TE Specific flag */
    data object EnableAlwaysAccept: AutoCallPickupMode(2)
}