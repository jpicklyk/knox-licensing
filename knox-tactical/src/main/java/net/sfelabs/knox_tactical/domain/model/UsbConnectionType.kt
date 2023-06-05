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
    object Default: UsbConnectionType(0)
    object MTP: UsbConnectionType(1)
    object PTP: UsbConnectionType(2)
    object MIDI: UsbConnectionType(3)
    object Charging: UsbConnectionType(4)
    object Tethering: UsbConnectionType(5)
}