package net.sfelabs.knox_tactical.domain.model

sealed class UsbDeviceAccessState {
    data object Enable: UsbDeviceAccessState()
    data object Disable: UsbDeviceAccessState()
    data object DisableAndClear: UsbDeviceAccessState()
}