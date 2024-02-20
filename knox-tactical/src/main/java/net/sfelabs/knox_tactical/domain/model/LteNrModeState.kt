package net.sfelabs.knox_tactical.domain.model

sealed class LteNrModeState(val value: Int) {
    companion object {
        operator fun invoke(type: Int): LteNrModeState {
            return when(type) {
                1 -> DisableSa
                2 -> DisableNsa
                else -> EnableBothSaAndNsa
            }
        }
    }

    data object EnableBothSaAndNsa: LteNrModeState(0)
    data object DisableNsa: LteNrModeState(2)
    data object DisableSa: LteNrModeState(1)
}