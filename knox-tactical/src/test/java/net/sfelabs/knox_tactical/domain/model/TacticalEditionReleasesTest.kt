package net.sfelabs.knox_tactical.domain.model

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for TacticalEditionReleases
 * Place this file in src/test/java/...
 */
class TacticalEditionReleasesTest {

    @Test
    fun testGetVersionInfoWithExactStringMatch() {
        val buildNumber = "G981U1UEU1ATF8_B2BF"
        val versionInfo = TacticalEditionReleases.getVersionInfo(buildNumber)

        assertEquals("TE2 Android 10 GA", versionInfo.description)
        assertEquals(100, versionInfo.releaseVersion)
        assertEquals(2, versionInfo.generation)
        assertEquals(buildNumber, versionInfo.buildNumber)
    }

    @Test
    fun testGetVersionInfoWithRegexPatternMatch() {
        val buildNumber = "S911U1UEU2AWL1_B2BF"
        val versionInfo = TacticalEditionReleases.getVersionInfo(buildNumber)

        assertEquals("TE3 Android 13 MR1 (beta)", versionInfo.description)
        assertEquals(131, versionInfo.releaseVersion)
        assertEquals(3, versionInfo.generation)
        assertEquals(buildNumber, versionInfo.buildNumber)
    }

    @Test
    fun testGetVersionInfoWithUnknownBuildNumber() {
        val buildNumber = "UNKNOWN_BUILD_NUMBER"
        val versionInfo = TacticalEditionReleases.getVersionInfo(buildNumber)

        assertTrue(versionInfo.description.contains("Unknown"))
        assertEquals(0, versionInfo.releaseVersion)
        assertEquals(0, versionInfo.generation)
        assertEquals(buildNumber, versionInfo.buildNumber)
    }

    @Test
    fun testGetAllVersions() {
        val allVersions = TacticalEditionReleases.getAllVersions()

        // Count of all defined version patterns (including regex patterns)
        assertTrue("Expected at least 20 versions", allVersions.size >= 20)
        assertTrue("Expected TE2 versions", allVersions.any { it.generation == 2 })
        assertTrue("Expected TE3 versions", allVersions.any { it.generation == 3 })
    }

    @Test
    fun testGetVersionsByGenerationTE2() {
        val te2Versions = TacticalEditionReleases.getVersionsByGeneration(2)

        assertFalse("Expected non-empty TE2 versions", te2Versions.isEmpty())
        assertTrue("All versions should be TE2", te2Versions.all { it.generation == 2 })
        assertTrue("Should contain TE2 in description", te2Versions.any { it.description.contains("TE2") })
    }

    @Test
    fun testGetVersionsByGenerationTE3() {
        val te3Versions = TacticalEditionReleases.getVersionsByGeneration(3)

        assertFalse("Expected non-empty TE3 versions", te3Versions.isEmpty())
        assertTrue("All versions should be TE3", te3Versions.all { it.generation == 3 })
        assertTrue("Should contain TE3 in description", te3Versions.any { it.description.contains("TE3") })
    }

    @Test
    fun testGetVersionsByReleaseVersion() {
        val releaseVersion = 131
        val versions = TacticalEditionReleases.getVersionsByReleaseVersion(releaseVersion)

        assertFalse("Expected versions with release version $releaseVersion", versions.isEmpty())
        assertTrue(
            "All versions should have release version $releaseVersion",
            versions.all { it.releaseVersion == releaseVersion }
        )
    }

    @Test
    fun testGetVersionsAboveReleaseVersion() {
        val threshold = 130
        val versions = TacticalEditionReleases.getVersionsAboveReleaseVersion(threshold)

        assertFalse("Expected versions above $threshold", versions.isEmpty())
        assertTrue(
            "All versions should be above $threshold",
            versions.all { it.releaseVersion > threshold }
        )
    }

    @Test
    fun testGetVersionsBelowReleaseVersion() {
        val threshold = 110
        val versions = TacticalEditionReleases.getVersionsBelowReleaseVersion(threshold)

        assertFalse("Expected versions below $threshold", versions.isEmpty())
        assertTrue(
            "All versions should be below $threshold",
            versions.all { it.releaseVersion < threshold }
        )
    }

    @Test
    fun testGetVersionsByDescriptionContaining() {
        val searchText = "MR1"
        val versions = TacticalEditionReleases.getVersionsByDescriptionContaining(searchText)

        assertFalse("Expected versions containing '$searchText'", versions.isEmpty())
        assertTrue(
            "All versions should contain '$searchText'",
            versions.all { it.description.contains(searchText, ignoreCase = true) }
        )
    }

    @Test
    fun testBetaVersionFiltering() {
        val betaVersions = TacticalEditionReleases.getVersionsByDescriptionContaining("beta")

        assertFalse("Expected beta versions", betaVersions.isEmpty())
        assertTrue(
            "All versions should contain 'beta'",
            betaVersions.all { it.description.lowercase().contains("beta") }
        )
    }

    @Test
    fun testTE3ModelNameAssignment() {
        val te3Versions = TacticalEditionReleases.getVersionsByGeneration(3)

        assertFalse("Expected TE3 versions", te3Versions.isEmpty())
        assertTrue("All TE3 versions should have correct model names",
            te3Versions.all { version ->
                version.modelName == "SM-S911U1" ||
                        version.modelName == "SM-G736U1" ||
                        version.modelName == "SM-X308U"
                        version.modelName.isEmpty()
            }
        )
    }
}