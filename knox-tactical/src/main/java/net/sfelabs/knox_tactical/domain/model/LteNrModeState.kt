package net.sfelabs.knox_tactical.domain.model

import com.samsung.android.knox.custom.CustomDeviceManager.ENABLE_BOTH_SA_AND_NSA

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

    object EnableBothSaAndNsa: LteNrModeState(0)
    object DisableNsa: LteNrModeState(2)
    object DisableSa: LteNrModeState(1)
}