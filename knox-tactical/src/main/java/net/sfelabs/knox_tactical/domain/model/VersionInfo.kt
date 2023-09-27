package net.sfelabs.knox_tactical.domain.model

sealed class VersionInfo(val description: String) {
    companion object {
        operator fun invoke(buildNumber: String): VersionInfo {
            return when(formatBuildNumber(buildNumber)) {
                "G981U1UEU1ATF8_B2BF" -> TE2Android10GA
                "G981U1UEU1ATL3_B2BF" -> TE2Android10MR1
                "G981U1UEU1AUG2_B2BF" -> TE2Android10MR2
                "G981U1UEU1AVB1_B2BF" -> TE2Android10MR3
                "G981U1UES2AVF2_B2BF" -> TE2Android10MR4
                "G981U1UES3AVF2_B2BF" -> TE2Android10MR4Special1
                "G981U1UES4AWC1_B2BF" -> TE2Android10MR4Special2
                "G981U1UEU3BVK1_B2BF" -> TE2Android11GA
                "G981U1UEU4BWC2_B2BF" -> TE2Android11MR1
                "G981U1UEU7BWI1_B2BF" -> TE2Android11MR2

                //TE3 Devices
                "G736U1UEU4CWH5_B2BF" -> TE3Android13GA
                "S911U1UEU1AWH5_B2BF" -> TE3Android13GA

                else -> Unknown
            }
        }
    }
    object Unknown: VersionInfo("Not a Tactical Edition device")
    object TE2Android10GA: VersionInfo("TE2 Android 10 GA")
    object TE2Android10MR1: VersionInfo("TE2 Android 10 MR1")
    object TE2Android10MR2: VersionInfo("TE2 Android 10 MR2")
    object TE2Android10MR3: VersionInfo("TE2 Android 10 MR3")
    //Extension program versions
    object TE2Android10MR4: VersionInfo("TE2 Android 10 MR4 (extension program)")
    object TE2Android10MR4Special1: VersionInfo("TE2 Android 10 MR4 (downgrade build 1)")
    object TE2Android10MR4Special2: VersionInfo("TE2 Android 10 MR4 (downgrade build 2)")
    object TE2Android11GA: VersionInfo("TE2 Android 11 GA (extension program)")
    object TE2Android11MR1: VersionInfo("TE2 Android 11 MR1 (extension program)")
    object TE2Android11MR2: VersionInfo("TE2 Android 11 MR2 (extension program)")
    object TE3Android13GA: VersionInfo("TE3 Android 13 GA")
}

private fun formatBuildNumber(buildNumber: String): String {
    return buildNumber.split(".").last()
}

fun isTacticalEditionGen2Device(buildNumber: String): Boolean {
    return when (VersionInfo(formatBuildNumber(buildNumber))) {
        VersionInfo.TE2Android10GA -> true
        VersionInfo.TE2Android10MR1 -> true
        VersionInfo.TE2Android10MR2 -> true
        VersionInfo.TE2Android10MR3 -> true
        VersionInfo.TE2Android10MR4 -> true
        VersionInfo.TE2Android10MR4Special1 -> true
        VersionInfo.TE2Android10MR4Special2 -> true
        VersionInfo.TE2Android11GA -> true
        VersionInfo.TE2Android11MR1-> true
        VersionInfo.TE2Android11MR2 -> true

        VersionInfo.TE3Android13GA -> false
        VersionInfo.Unknown -> false
    }
}

fun isTacticalEditionGen3Device(buildNumber: String): Boolean {
    return when (VersionInfo(formatBuildNumber(buildNumber))) {
        VersionInfo.TE3Android13GA -> true

        VersionInfo.TE2Android10GA -> false
        VersionInfo.TE2Android10MR1 -> false
        VersionInfo.TE2Android10MR2 -> false
        VersionInfo.TE2Android10MR3 -> false
        VersionInfo.TE2Android10MR4 -> false
        VersionInfo.TE2Android10MR4Special1 -> false
        VersionInfo.TE2Android10MR4Special2 -> false
        VersionInfo.TE2Android11GA -> false
        VersionInfo.TE2Android11MR1-> false
        VersionInfo.TE2Android11MR2 -> false

        VersionInfo.Unknown -> false
    }
}