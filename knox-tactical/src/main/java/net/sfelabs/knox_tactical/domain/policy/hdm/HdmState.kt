package net.sfelabs.knox_tactical.domain.policy.hdm

import net.sfelabs.core.domain.usecase.model.ApiError
import net.sfelabs.core.knox.feature.api.PolicyState

data class HdmState(
    override val isEnabled: Boolean,
    override val isSupported: Boolean = true,
    override val error: ApiError? = null,
    override val exception: Throwable? = null,
    val policyMask: Int = 0
) : PolicyState {
    companion object {
        const val CAMERA_MASK = 1      // 2^0
        const val EXTERNAL_MEMORY_MASK = 2 // 2^1
        const val USB_MASK = 4          // 2^2
        const val WIFI_MASK = 8         // 2^3
        const val BLUETOOTH_MASK = 16   // 2^4
        const val GPS_MASK = 32        // 2^5
        const val NFC_MASK = 64        // 2^6
        const val MICROPHONE_MASK = 128 // 2^7
        const val MODEM_MASK = 256     // 2^8
        const val SPEAKER_MASK = 512   // 2^9
    }
}
