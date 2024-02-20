package net.sfelabs.knox_tactical.domain.model

sealed class UsbConnectionType(val value: Int) {
    companion object {
        operator fun invoke(type: Int): UsbConnectionType {
            return when(type) {
                1 -> MTP
                2 -> PTP
                3 -> MIDI
                4 -> Charging
                5 -> Tethering
                else -> Default
            }
        }
    }
    data object Default: UsbConnectionType(0)
    data object MTP: UsbConnectionType(1)
    data object PTP: UsbConnectionType(2)
    data object MIDI: UsbConnectionType(3)
    data object Charging: UsbConnectionType(4)
    data object Tethering: UsbConnectionType(5)
}