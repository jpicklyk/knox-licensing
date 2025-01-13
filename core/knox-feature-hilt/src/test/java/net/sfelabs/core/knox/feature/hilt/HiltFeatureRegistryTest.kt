package net.sfelabs.core.knox.feature.hilt

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.internal.component.FeatureComponent
import net.sfelabs.core.knox.feature.internal.handler.FeatureHandler
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.FeatureKey
import net.sfelabs.core.knox.feature.internal.model.FeatureState
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class HiltFeatureRegistryTest {
    private lateinit var hiltRegistry: HiltFeatureRegistry
    private val mockComponent = mockk<FeatureComponent<Boolean>>()
    private val mockKey = mockk<FeatureKey<Boolean>>()
    private val mockHandler = mockk<FeatureHandler<Boolean>>()

    @Before
    fun setup() {
        hiltRegistry = HiltFeatureRegistry()

        // Setup mock component
        every { mockComponent.featureName } returns "test_feature"
        every { mockComponent.key } returns mockKey
        every { mockComponent.handler } returns mockHandler
        every { mockComponent.category } returns FeatureCategory.Toggle
        every { mockKey.featureName } returns "test_feature"
        coEvery { mockHandler.getState() } returns ApiResult.Success(FeatureState(true, true))
    }

    @Test
    fun `setComponents properly initializes delegate`() {
        // When
        hiltRegistry.setComponents(setOf(mockComponent))

        // Then
        assertTrue(hiltRegistry.isRegistered(mockKey))
        assertEquals(mockHandler, hiltRegistry.getHandler(mockKey))
    }

    @Test
    fun `getFeatures returns features from correct category`() = runTest {
        // Given
        every { mockComponent.category } returns FeatureCategory.Toggle
        hiltRegistry.setComponents(setOf(mockComponent))

        // When
        val features = hiltRegistry.getFeatures(FeatureCategory.Toggle)

        // Then
        assertEquals(1, features.size)
    }

    @Test
    fun `getComponent returns correct component`() {
        // Given
        hiltRegistry.setComponents(setOf(mockComponent))

        // When
        val component = hiltRegistry.getComponent(mockKey)

        // Then
        assertEquals(mockComponent, component)
    }

    @Test
    fun `setComponents updates existing components`() = runTest {
        // Given
        val component1 = mockk<FeatureComponent<Boolean>>()
        val component2 = mockk<FeatureComponent<Boolean>>()

        val handler1 = mockk<FeatureHandler<Boolean>>()
        val handler2 = mockk<FeatureHandler<Boolean>>()
        val key1 = mockk<FeatureKey<Boolean>>()
        val key2 = mockk<FeatureKey<Boolean>>()

        // Configure component1
        every { component1.featureName } returns "feature1"
        every { component1.category } returns FeatureCategory.Toggle
        every { component1.key } returns key1
        every { component1.handler } returns handler1
        every { key1.featureName } returns "feature1"
        coEvery { handler1.getState() } returns ApiResult.Success(FeatureState(true, true))

        // Configure component2
        every { component2.featureName } returns "feature2"
        every { component2.category } returns FeatureCategory.Toggle
        every { component2.key } returns key2
        every { component2.handler } returns handler2
        every { key2.featureName } returns "feature2"
        coEvery { handler2.getState() } returns ApiResult.Success(FeatureState(true, true))

        // When
        hiltRegistry.setComponents(setOf(component1))
        hiltRegistry.setComponents(setOf(component2))

        // Then
        val components = mutableListOf<FeatureComponent<*>>()
        hiltRegistry.getFeatures(FeatureCategory.Toggle).forEach {
            hiltRegistry.getComponent(it.key)?.let { component ->
                components.add(component)
            }
        }
        assertEquals(1, components.size)
        assertEquals(component2, components.first())
    }
}