package net.sfelabs.knox_tactical.domain.model

sealed class UsbDeviceAccessState {
    object Enable: UsbDeviceAccessState()
    object Disable: UsbDeviceAccessState()
    object DisableAndClear: UsbDeviceAccessState()
}