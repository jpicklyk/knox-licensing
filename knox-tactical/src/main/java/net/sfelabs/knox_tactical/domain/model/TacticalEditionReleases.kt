package net.sfelabs.knox_tactical.domain.model


object TacticalEditionReleases {
    private val versionPatterns = mapOf(
        // TE 2 Program
        "G981U1UEU1ATF8_B2BF" to VersionInfo.Builder()
            .description("TE2 Android 10 GA")
            .releaseVersion(100)
            .generation(2)
            .buildNumber("G981U1UEU1ATF8_B2BF"),
        "G981U1UEU1ATL3_B2BF" to VersionInfo.Builder()
            .description("TE2 Android 10 MR1")
            .releaseVersion(101)
            .generation(2)
            .buildNumber("G981U1UEU1ATL3_B2BF"),
        "G981U1UEU1AUG2_B2BF" to VersionInfo.Builder()
            .description("TE2 Android 10 MR2")
            .releaseVersion(102)
            .generation(2)
            .buildNumber("G981U1UEU1AUG2_B2BF"),
        "G981U1UEU1AVB1_B2BF" to VersionInfo.Builder()
            .description("TE2 Android 10 MR3")
            .releaseVersion(103)
            .generation(2)
            .buildNumber("G981U1UEU1AVB1_B2BF"),
        "G981U1UEU3BVK1_B2BF" to VersionInfo.Builder()
            .description("TE2 Android 11 GA (extension program)")
            .releaseVersion(110)
            .generation(2)
            .buildNumber("G981U1UEU3BVK1_B2BF"),
        "G981U1UEU4BWC2_B2BF" to VersionInfo.Builder()
            .description("TE2 Android 11 MR1 (extension program)")
            .releaseVersion(111)
            .generation(2)
            .buildNumber("G981U1UEU4BWC2_B2BF"),
        "G981U1UEU7BWI1_B2BF" to VersionInfo.Builder()
            .description("TE2 Android 11 MR2 (extension program)")
            .releaseVersion(112)
            .generation(2)
            .buildNumber("G981U1UEU7BWI1_B2BF"),
        "G981U1UEU8BXC1_B2BF" to VersionInfo.Builder()
            .description("TE2 Android 11 MR3 (extension program)")
            .releaseVersion(113)
            .generation(2)
            .buildNumber("G981U1UEU8BXC1_B2BF"),
        "G981U1UES2AVF2_B2BF" to VersionInfo.Builder()
            .description("TE2 Android 10 MR4 (extension program)")
            .releaseVersion(104)
            .generation(2)
            .buildNumber("G981U1UES2AVF2_B2BF"),
        "G981U1UES3AVF2_B2BF" to VersionInfo.Builder()
            .description("TE2 Android 10 MR4 (downgrade build 1)")
            .releaseVersion(104)
            .generation(2)
            .buildNumber("G981U1UES3AVF2_B2BF"),
        "G981U1UES4AWC1_B2BF" to VersionInfo.Builder()
            .description("TE2 Android 10 MR4 (downgrade build 2)")
            .releaseVersion(104)
            .generation(2)
            .buildNumber("G981U1UES4AWC1_B2BF"),

        // TE3 Program
        "S911U1UEU1AWH5_B2BF" to VersionInfo.Builder()
            .description("TE3 Android 13 GA")
            .releaseVersion(130)
            .generation(3)
            .modelName("SM-S911U1")
            .buildNumber(""),
        "G736U1UEU4CWH5_B2BF" to VersionInfo.Builder()
            .description("TE3 Android 13 GA")
            .releaseVersion(130)
            .generation(3)
            .modelName("SM-G736U1")
            .buildNumber(""),
        Regex("(S911U1UEU2A(WL1|XA[1-3])|G736U1UEU5C(WL1|XA[1-3]))_B2BF") to VersionInfo.Builder()
            .description("TE3 Android 13 MR1 (beta)")
            .releaseVersion(131)
            .generation(3)
            .buildNumber(""),
        "S911U1UEU2AXA4_B2BF" to VersionInfo.Builder()
            .description("TE3 Android 13 MR1")
            .releaseVersion(131)
            .generation(3)
            .modelName("SM-S911U1")
            .buildNumber("S911U1UEU2AXA4_B2BF"),
        "G736U1UEU5CXB1_B2BF" to VersionInfo.Builder()
            .description("TE3 Android 13 MR1")
            .releaseVersion(131)
            .generation(3)
            .modelName("SM-G736U1")
            .buildNumber("G736U1UEU5CXB1_B2BF"),
        Regex("(G736U1UEU[67]CX[FG][1-9]_B2BF)") to VersionInfo.Builder()
            .description("TE3 Android 13 MR2 beta")
            .releaseVersion(132)
            .generation(3)
            .modelName("SM-G736U1")
            .buildNumber(""),
        Regex("(S911U1UEU4AXG[1-9]_B2BF)") to VersionInfo.Builder()
            .description("TE3 Android 13 MR2 beta")
            .releaseVersion(132)
            .generation(3)
            .modelName("SM-S911U1")
            .buildNumber(""),
        "S911U1UEU4AXH1_B2BF" to VersionInfo.Builder()
            .description("TE3 Android 13 MR2")
            .releaseVersion(132)
            .generation(3)
            .modelName("SM-S911U1")
            .buildNumber("S911U1UEU4AXH1_B2BF"),
        "G736U1UEU7CXH3_B2BF" to VersionInfo.Builder()
            .description("TE3 Android 13 MR2")
            .releaseVersion(132)
            .generation(3)
            .modelName("SM-G736U1")
            .buildNumber("G736U1UEU7CXH3_B2BF"),
    )

    fun getVersionInfo(buildNumber: String): VersionInfo {
        val formattedBuildNumber = buildNumber.split(".").last()
        return versionPatterns.entries.find { (key, _) ->
            when (key) {
                is String -> key.equals(formattedBuildNumber)
                is Regex -> key.matches(formattedBuildNumber)
                else -> false
            }
        }?.value?.buildNumber(formattedBuildNumber)?.build()
            ?: VersionInfo.Builder.createDefaultVersion(formattedBuildNumber)
    }

    fun getAllVersions(): List<VersionInfo> {
        return versionPatterns.values.map { it.build() }
    }

    fun filterVersions(predicate: (VersionInfo) -> Boolean): List<VersionInfo> {
        return getAllVersions().filter(predicate)
    }

    fun getVersionsByGeneration(generation: Int): List<VersionInfo> {
        return filterVersions { it.generation == generation }
    }

    fun getVersionsByReleaseVersion(releaseVersion: Int): List<VersionInfo> {
        return filterVersions { it.releaseVersion == releaseVersion }
    }

    fun getVersionsAboveReleaseVersion(releaseVersion: Int): List<VersionInfo> {
        return filterVersions { it.releaseVersion > releaseVersion }
    }

    fun getVersionsBelowReleaseVersion(releaseVersion: Int): List<VersionInfo> {
        return filterVersions { it.releaseVersion < releaseVersion }
    }

    fun getVersionsByDescriptionContaining(text: String): List<VersionInfo> {
        return filterVersions { it.description.contains(text, ignoreCase = true) }
    }
}