package net.sfelabs.knox_tactical.domain.policy.hdm

enum class HdmComponent(val mask: Int, val displayName: String) {
    CAMERA(HdmState.CAMERA_MASK, "Camera"),
    EXTERNAL_MEMORY(HdmState.EXTERNAL_MEMORY_MASK, "External Memory"),
    USB(HdmState.USB_MASK, "USB"),
    WIFI(HdmState.WIFI_MASK, "Wi-Fi"),
    BLUETOOTH(HdmState.BLUETOOTH_MASK, "Bluetooth"),
    GPS(HdmState.GPS_MASK, "GPS"),
    NFC(HdmState.NFC_MASK, "NFC"),
    MICROPHONE(HdmState.MICROPHONE_MASK, "Microphone"),
    MODEM(HdmState.MODEM_MASK, "Modem"),
    SPEAKER(HdmState.SPEAKER_MASK, "Speaker")
}