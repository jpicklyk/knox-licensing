package net.sfelabs.knox_tactical.domain.model

sealed class TacticalFeature(val key: String) {
    companion object {
        operator fun invoke(key: String): TacticalFeature? {
            return when (key) {
                "TacticalDeviceMode" -> TacticalDeviceMode
                "AutoSensitivity" -> AutoSensitivity
                "Hotspot20" -> Hotspot20
                "RamPlus" -> RamPlus
                "RandomMac" -> RandomMac
                "LteBandLock" -> LteBandLock
                else -> null
            }
        }
    }
    object TacticalDeviceMode: TacticalFeature("TacticalDeviceMode")
    object AutoSensitivity: TacticalFeature("AutoSensitivity")
    object Hotspot20: TacticalFeature("Hotspot20")
    object RamPlus: TacticalFeature("RamPlus")
    object RandomMac: TacticalFeature("RandomMac")
    object LteBandLock: TacticalFeature("LteBandLock")

}