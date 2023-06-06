package net.sfelabs.knox_tactical.domain.model

sealed class AutoCallPickupState(val value: Int) {
    companion object {
        operator fun invoke(type: Int): AutoCallPickupState {
            return when(type) {
                0 -> Disable
                1 -> Enable
                else -> EnableAlwaysAccept
            }
        }
    }
    object Disable: AutoCallPickupState(0)
    object Enable: AutoCallPickupState(1)
    /* TE Specific flag */
    object EnableAlwaysAccept: AutoCallPickupState(2)
}