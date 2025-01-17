package net.sfelabs.knox_tactical.domain.model

sealed class LteNrMode(val value: Int) {
    companion object {
        operator fun invoke(type: Int): LteNrMode {
            return when(type) {
                1 -> DisableSa
                2 -> DisableNsa
                else -> EnableBothSaAndNsa
            }
        }
    }

    data object EnableBothSaAndNsa: LteNrMode(0)
    data object DisableNsa: LteNrMode(2)
    data object DisableSa: LteNrMode(1)
}