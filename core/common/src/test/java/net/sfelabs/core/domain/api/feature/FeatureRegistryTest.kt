package net.sfelabs.core.domain.api.feature

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FeatureRegistryTest {

    private lateinit var featureRegistry: FeatureRegistry

    @Before
    fun setUp() {
        featureRegistry = FeatureRegistry()
    }

    @Test
    fun registerFeature_shouldAddFeatureToCorrectCategory() {
        featureRegistry.registerFeature(TestFeatureKey.TestFeature1, FeatureCategory.PRODUCTION)
        featureRegistry.registerFeature(TestFeatureKey.TestFeature2, FeatureCategory.EXPERIMENTAL)

        val productionFeatures = featureRegistry.getFeatures(FeatureCategory.PRODUCTION)
        val experimentalFeatures = featureRegistry.getFeatures(FeatureCategory.EXPERIMENTAL)

        assertTrue(TestFeatureKey.TestFeature1 in productionFeatures)
        assertTrue(TestFeatureKey.TestFeature2 in experimentalFeatures)
    }

    @Test
    fun getFeatures_shouldReturnAllFeaturesWhenNoCategorySpecified() {
        featureRegistry.registerFeature(TestFeatureKey.TestFeature1, FeatureCategory.PRODUCTION)
        featureRegistry.registerFeature(TestFeatureKey.TestFeature2, FeatureCategory.EXPERIMENTAL)

        val allFeatures = featureRegistry.getFeatures()

        assertEquals(2, allFeatures.size)
        assertTrue(TestFeatureKey.TestFeature1 in allFeatures)
        assertTrue(TestFeatureKey.TestFeature2 in allFeatures)
    }

    @Test
    fun getCategorizedFeatures_shouldReturnFeaturesGroupedByCategory() {
        featureRegistry.registerFeature(TestFeatureKey.TestFeature1, FeatureCategory.PRODUCTION)
        featureRegistry.registerFeature(TestFeatureKey.TestFeature2, FeatureCategory.EXPERIMENTAL)

        val categorizedFeatures = featureRegistry.getCategorizedFeatures()

        assertEquals(2, categorizedFeatures.size)
        assertTrue(TestFeatureKey.TestFeature1 in categorizedFeatures[FeatureCategory.PRODUCTION]!!)
        assertTrue(TestFeatureKey.TestFeature2 in categorizedFeatures[FeatureCategory.EXPERIMENTAL]!!)
    }
}