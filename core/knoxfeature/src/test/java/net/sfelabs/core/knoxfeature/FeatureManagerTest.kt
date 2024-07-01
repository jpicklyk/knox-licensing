package net.sfelabs.core.knoxfeature

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.knoxfeature.domain.FeatureCategory
import net.sfelabs.core.knoxfeature.domain.FeatureHandler
import net.sfelabs.core.knoxfeature.domain.FeatureHandlerFactory
import net.sfelabs.core.knoxfeature.domain.FeatureManager
import net.sfelabs.core.knoxfeature.domain.FeatureRegistry
import net.sfelabs.core.knoxfeature.model.FeatureState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FeatureManagerTest {

    private lateinit var featureHandlerFactory: FeatureHandlerFactory
    private lateinit var featureRegistry: FeatureRegistry
    private lateinit var featureManager: FeatureManager

    @Before
    fun setUp() {
        featureHandlerFactory = mockk()
        featureRegistry = mockk()
        featureManager = FeatureManager(featureHandlerFactory, featureRegistry)
    }

    @Test
    fun getFeatureState_shouldReturnStateFromHandler() = runTest {
        val testHandler = mockk<FeatureHandler<Boolean>>()
        coEvery { featureHandlerFactory.getHandler(any<FeatureKey<Boolean>>()) } returns testHandler
        coEvery { testHandler.getState() } returns ApiResult.Success(FeatureState(true, true))

        val result = featureManager.getFeatureState(TestFeatureKey.TestFeature1)

        assertTrue(result is ApiResult.Success)
        assertEquals(FeatureState(true, true), (result as ApiResult.Success).data)
    }

    @Test
    fun setFeatureState_shouldCallSetStateOnHandler() = runTest {
        val testHandler = mockk<FeatureHandler<Boolean>>()
        coEvery { featureHandlerFactory.getHandler(any<FeatureKey<Boolean>>()) } returns testHandler
        coEvery { testHandler.setState(any()) } returns ApiResult.Success(Unit)

        val newState = FeatureState(false, false)
        val result = featureManager.setFeatureState(TestFeatureKey.TestFeature1, newState)

        assertTrue(result is ApiResult.Success)
        coVerify { testHandler.setState(newState) }
    }

    @Test
    fun getAllFeatures_shouldReturnFeaturesFromRegistry() = runTest {
        val testHandler = mockk<FeatureHandler<Boolean>>()
        coEvery { featureHandlerFactory.getHandler(any<FeatureKey<Boolean>>()) } returns testHandler
        coEvery { testHandler.getState() } returns ApiResult.Success(FeatureState(true, true))

        every { featureRegistry.getFeatures(null) } returns setOf(
            TestFeatureKey.TestFeature1,
            TestFeatureKey.TestFeature2
        )

        val result = featureManager.getAllFeatures()

        assertTrue(result is ApiResult.Success)
        assertEquals(2, (result as ApiResult.Success).data.size)
        assertTrue(result.data.all { true })
        assertEquals(setOf(TestFeatureKey.TestFeature1, TestFeatureKey.TestFeature2),
            result.data.map { it.key }.toSet())
    }

    @Test
    fun getAllCategorizedFeatures_shouldReturnCategorizedFeaturesFromRegistry() = runTest {
        val testHandler = mockk<FeatureHandler<Boolean>>()
        coEvery { featureHandlerFactory.getHandler(any<FeatureKey<Boolean>>()) } returns testHandler
        coEvery { testHandler.getState() } returns ApiResult.Success(FeatureState(true, true))

        every { featureRegistry.getCategorizedFeatures() } returns mapOf(
            FeatureCategory.PRODUCTION to setOf(TestFeatureKey.TestFeature1),
            FeatureCategory.EXPERIMENTAL to setOf(TestFeatureKey.TestFeature2)
        )

        val result = featureManager.getAllCategorizedFeatures()

        assertTrue(result is ApiResult.Success)
        val categorizedFeatures = (result as ApiResult.Success).data
        assertEquals(2, categorizedFeatures.size)

        val productionFeatures = categorizedFeatures[FeatureCategory.PRODUCTION]
        assertEquals(1, productionFeatures?.size)
        assertTrue(productionFeatures?.first() is Feature<*>)
        assertEquals(TestFeatureKey.TestFeature1, (productionFeatures?.first() as Feature<*>).key)

        val experimentalFeatures = categorizedFeatures[FeatureCategory.EXPERIMENTAL]
        assertEquals(1, experimentalFeatures?.size)
        assertTrue(experimentalFeatures?.first() is Feature<*>)
        assertEquals(TestFeatureKey.TestFeature2, (experimentalFeatures?.first() as Feature<*>).key)
    }

    // Define test classes and objects
    sealed class TestFeatureKey : FeatureKey<Boolean> {
        data object TestFeature1 : TestFeatureKey() {
            override val featureName: String = "test_feature_1"
        }
        data object TestFeature2 : TestFeatureKey() {
            override val featureName: String = "test_feature_2"
        }
    }
}