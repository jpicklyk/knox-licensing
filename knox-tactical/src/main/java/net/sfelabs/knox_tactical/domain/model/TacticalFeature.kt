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
                "SimPowerState" -> SimPowerState
                else -> null
            }
        }
    }
    data object TacticalDeviceMode: TacticalFeature("TacticalDeviceMode")
    data object AutoSensitivity: TacticalFeature("AutoSensitivity")
    data object Hotspot20: TacticalFeature("Hotspot20")
    data object RamPlus: TacticalFeature("RamPlus")
    data object RandomMac: TacticalFeature("RandomMac")
    data object LteBandLock: TacticalFeature("LteBandLock")
    data object SimPowerState: TacticalFeature("SimPowerState")

}