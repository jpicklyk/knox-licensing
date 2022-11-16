package net.sfelabs.knox_tactical.domain.model

import com.samsung.android.knox.custom.CustomDeviceManager

sealed class AdbHeader(val value: String) {
    object IP: AdbHeader(CustomDeviceManager.CMD_HEADER_IP)
    object PPPD: AdbHeader(CustomDeviceManager.CMD_HEADER_PPPD)
    object DHCPDBG: AdbHeader(CustomDeviceManager.CMD_HEADER_DHCPDBG)
}
