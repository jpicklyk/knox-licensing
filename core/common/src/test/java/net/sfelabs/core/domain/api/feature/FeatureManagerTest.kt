package net.sfelabs.core.domain.api.feature

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import net.sfelabs.core.domain.api.ApiResult
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
    fun getFeatureState_shouldReturnStateFromHandler() = runBlocking {
        val testHandler = mockk<FeatureHandler<Boolean>>()
        coEvery { featureHandlerFactory.getHandler<Boolean>(any()) } returns testHandler
        coEvery { testHandler.getState() } returns ApiResult.Success(FeatureState(true, true))

        val result = featureManager.getFeatureState(TestFeatureKey.TestFeature1)

        assertTrue(result is ApiResult.Success)
        assertEquals(FeatureState(true, true), (result as ApiResult.Success).data)
    }

    @Test
    fun setFeatureState_shouldCallSetStateOnHandler() = runBlocking {
        val testHandler = mockk<FeatureHandler<Boolean>>()
        coEvery { featureHandlerFactory.getHandler<Boolean>(any()) } returns testHandler
        coEvery { testHandler.setState(any()) } returns ApiResult.Success(Unit)

        val newState = FeatureState(false, false)
        val result = featureManager.setFeatureState(TestFeatureKey.TestFeature1, newState)

        assertTrue(result is ApiResult.Success)
        coVerify { testHandler.setState(newState) }
    }

    @Test
    fun getAllFeatures_shouldReturnFeaturesFromRegistry() = runBlocking {
        val testHandler = TestFeatureHandler()
        coEvery { featureHandlerFactory.getHandler<Boolean>(any()) } returns testHandler
        every { featureRegistry.getFeatures(null) } returns setOf(TestFeatureKey.TestFeature1, TestFeatureKey.TestFeature2)

        val result = featureManager.getAllFeatures()

        assertTrue(result is ApiResult.Success)
        assertEquals(2, (result as ApiResult.Success).data.size)
    }

    @Test
    fun getAllCategorizedFeatures_shouldReturnCategorizedFeaturesFromRegistry() = runBlocking {
        val testHandler = TestFeatureHandler()
        coEvery { featureHandlerFactory.getHandler<Boolean>(any()) } returns testHandler
        every { featureRegistry.getCategorizedFeatures() } returns mapOf(
            FeatureCategory.PRODUCTION to setOf(TestFeatureKey.TestFeature1),
            FeatureCategory.EXPERIMENTAL to setOf(TestFeatureKey.TestFeature2)
        )

        val result = featureManager.getAllCategorizedFeatures()

        assertTrue(result is ApiResult.Success)
        assertEquals(2, (result as ApiResult.Success).data.size)
        assertEquals(1, result.data[FeatureCategory.PRODUCTION]?.size)
        assertEquals(1, result.data[FeatureCategory.EXPERIMENTAL]?.size)
    }
}