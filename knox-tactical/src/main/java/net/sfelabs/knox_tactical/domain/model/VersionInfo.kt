package net.sfelabs.knox_tactical.domain.model

sealed class VersionInfo(val description: String, val releaseVersion: Int) {
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
                "G981U1UEU8BXA2_B2BF" -> TE2Android11MR3

                //TE3 Devices
                "G736U1UEU4CWH5_B2BF" -> TE3Android13GA
                "S911U1UEU1AWH5_B2BF" -> TE3Android13GA
                "S911U1UEU2AWL1_B2BF" -> TE3Android13MR1Beta
                "G736U1UEU5CWL1_B2BF" -> TE3Android13MR1Beta
                "S911U1UEU2AXA1_B2BF" -> TE3Android13MR1Beta
                "G736U1UEU5CXA1_B2BF" -> TE3Android13MR1Beta
                "S911U1UEU2AXA2_B2BF" -> TE3Android13MR1Beta
                "G736U1UEU5CXA2_B2BF" -> TE3Android13MR1Beta
                "S911U1UEU2AXA3_B2BF" -> TE3Android13MR1Beta
                "G736U1UEU5CXA3_B2BF" -> TE3Android13MR1Beta
                "S911U1UEU2AXA4_B2BF" -> TE3Android13MR1

                else -> Unknown
            }
        }
    }
    data object Unknown: VersionInfo("Not a Tactical Edition device", 0)
    data object TE2Android10GA: VersionInfo("TE2 Android 10 GA", 100)
    data object TE2Android10MR1: VersionInfo("TE2 Android 10 MR1", 101)
    data object TE2Android10MR2: VersionInfo("TE2 Android 10 MR2", 102)
    data object TE2Android10MR3: VersionInfo("TE2 Android 10 MR3", 103)
    //Extension program versions
    data object TE2Android10MR4: VersionInfo("TE2 Android 10 MR4 (extension program)", 104)
    data object TE2Android10MR4Special1: VersionInfo("TE2 Android 10 MR4 (downgrade build 1)", 104)
    data object TE2Android10MR4Special2: VersionInfo("TE2 Android 10 MR4 (downgrade build 2)", 104)
    data object TE2Android11GA: VersionInfo("TE2 Android 11 GA (extension program)", 110)
    data object TE2Android11MR1: VersionInfo("TE2 Android 11 MR1 (extension program)", 111)
    data object TE2Android11MR2: VersionInfo("TE2 Android 11 MR2 (extension program)", 112)
    data object TE2Android11MR3: VersionInfo("TE2 Android 11 MR3 (extension program)", 113)
    data object TE3Android13GA: VersionInfo("TE3 Android 13 GA", 130)
    data object TE3Android13MR1Beta: VersionInfo("TE3 Android 13 MR1 (beta)", 131)
    data object TE3Android13MR1: VersionInfo("TE3 Android 13 MR1", 131)
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
        VersionInfo.TE2Android11MR3 -> true

        else -> false

    }
}

fun isTacticalEditionGen3Device(buildNumber: String): Boolean {
    return when (VersionInfo(formatBuildNumber(buildNumber))) {
        VersionInfo.TE3Android13GA -> true
        VersionInfo.TE3Android13MR1Beta -> true
        VersionInfo.TE3Android13MR1 -> true
        else -> false

    }
}

fun isTacticalEditionDevice(buildNumber: String): Boolean {
    return isTacticalEditionGen2Device(buildNumber) or isTacticalEditionGen3Device(buildNumber)
}