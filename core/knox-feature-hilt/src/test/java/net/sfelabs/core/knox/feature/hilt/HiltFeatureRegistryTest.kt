package net.sfelabs.core.knox.feature.hilt

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.usecase.model.ApiError
import net.sfelabs.core.knox.feature.api.PolicyComponent
import net.sfelabs.core.knox.feature.domain.usecase.handler.PolicyHandler
import net.sfelabs.core.knox.feature.api.PolicyCategory
import net.sfelabs.core.knox.feature.api.PolicyKey
import net.sfelabs.core.knox.feature.api.PolicyState
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class HiltFeatureRegistryTest {
    data class TestState(
        override val isEnabled: Boolean,
        override val isSupported: Boolean = true,
        override val error: ApiError? = null,
        override val exception: Throwable? = null
    ) : PolicyState {
        override fun withEnabled(enabled: Boolean): PolicyState {
            return copy(isEnabled = enabled)
        }

        override fun withError(
            error: ApiError?,
            exception: Throwable?
        ): PolicyState {
            return copy(error = error, exception = exception)
        }
    }

    private lateinit var hiltRegistry: HiltPolicyRegistry
    private val mockComponent = mockk<PolicyComponent<PolicyState>>()
    private val mockKey = mockk<PolicyKey<PolicyState>>()
    private val mockHandler = mockk<PolicyHandler<PolicyState>>()

    @Before
    fun setup() {
        hiltRegistry = HiltPolicyRegistry()

        // Setup mock component
        every { mockComponent.policyName } returns "test_feature"
        every { mockComponent.key } returns mockKey
        every { mockComponent.handler } returns mockHandler
        every { mockComponent.category } returns PolicyCategory.Toggle
        every { mockKey.policyName } returns "test_feature"
        coEvery { mockHandler.getState() } returns TestState(isEnabled = true)
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
        every { mockComponent.category } returns PolicyCategory.Toggle
        hiltRegistry.setComponents(setOf(mockComponent))

        // When
        val features = hiltRegistry.getPolicies(PolicyCategory.Toggle)

        // Then
        assertEquals(1, features.size)
        assertTrue(features[0].state.isEnabled)
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
        val component1 = mockk<PolicyComponent<PolicyState>>()
        val component2 = mockk<PolicyComponent<PolicyState>>()

        val handler1 = mockk<PolicyHandler<PolicyState>>()
        val handler2 = mockk<PolicyHandler<PolicyState>>()
        val key1 = mockk<PolicyKey<PolicyState>>()
        val key2 = mockk<PolicyKey<PolicyState>>()

        // Configure component1
        every { component1.policyName } returns "feature1"
        every { component1.category } returns PolicyCategory.Toggle
        every { component1.key } returns key1
        every { component1.handler } returns handler1
        every { key1.policyName } returns "feature1"
        coEvery { handler1.getState() } returns TestState(isEnabled = true)

        // Configure component2
        every { component2.policyName } returns "feature2"
        every { component2.category } returns PolicyCategory.Toggle
        every { component2.key } returns key2
        every { component2.handler } returns handler2
        every { key2.policyName } returns "feature2"
        coEvery { handler2.getState() } returns TestState(isEnabled = true)

        // When
        hiltRegistry.setComponents(setOf(component1))
        hiltRegistry.setComponents(setOf(component2))

        // Then
        val components = mutableListOf<PolicyComponent<*>>()
        hiltRegistry.getPolicies(PolicyCategory.Toggle).forEach {
            hiltRegistry.getComponent(it.key)?.let { component ->
                components.add(component)
            }
        }
        assertEquals(1, components.size)
        assertEquals(component2, components.first())
    }
}