package net.sfelabs.knox_common.domain.model

sealed class TargetKeystore(val value: Int) {
    object VpnAndApps : TargetKeystore(4)
    object Wifi : TargetKeystore(2)
    object Default : TargetKeystore(1)
}
