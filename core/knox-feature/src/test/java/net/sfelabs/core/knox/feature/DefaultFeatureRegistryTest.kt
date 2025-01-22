package net.sfelabs.core.knox.feature

import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.usecase.model.ApiError
import net.sfelabs.core.domain.usecase.model.DefaultApiError
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.FeatureComponent
import net.sfelabs.core.knox.feature.api.FeatureKey
import net.sfelabs.core.knox.feature.api.PolicyState
import net.sfelabs.core.knox.feature.data.repository.DefaultFeatureRegistry
import net.sfelabs.core.knox.feature.domain.usecase.handler.FeatureHandler
import org.junit.Before
import org.junit.Test

class DefaultFeatureRegistryTest {
    // Test PolicyState implementation
    data class TestState(
        override val isEnabled: Boolean,
        override val isSupported: Boolean = true,
        override val error: ApiError? = null,
        override val exception: Throwable? = null
    ) : PolicyState {
        override fun withEnabled(enabled: Boolean): PolicyState {
            return copy(isEnabled = enabled)
        }

        override fun withError(error: ApiError?, exception: Throwable?): PolicyState {
            return copy(error = error, exception = exception)
        }
    }

    private lateinit var registry: DefaultFeatureRegistry
    private lateinit var mockComponent: FeatureComponent<PolicyState>
    private val mockKey = object : FeatureKey<PolicyState> {
        override val featureName = "test_feature"
    }
    private val mockHandler = mockk<FeatureHandler<PolicyState>>()

    @Before
    fun setup() {
        registry = DefaultFeatureRegistry()
        mockComponent = object : FeatureComponent<PolicyState> {
            override val featureName = "test_feature"
            override val title = "Test Feature"
            override val description = "some description"
            override val category = FeatureCategory.Toggle
            override val handler = mockHandler
            override val defaultValue = TestState(isEnabled = false)
            override val key = mockKey
        }
    }

    @Test
    fun `when getting features by category then only matching features are returned`() = runTest {
        coEvery { mockHandler.getState() } returns TestState(isEnabled = true)

        registry.components = setOf(mockComponent)

        val features = registry.getFeatures(FeatureCategory.Toggle)
        assertEquals(1, features.size)
        assertEquals(mockKey, features[0].key)
        assertTrue(features[0].state.isEnabled)
    }

    @Test
    fun `when getting non-existent feature then returns null handler`() {
        registry.components = emptySet()
        assertNull(registry.getHandler(mockKey))
    }

    @Suppress("USELESS_CAST")
    @Test
    fun `when feature state has error then returns feature with error state`() = runTest {
        val error = DefaultApiError.UnexpectedError()
        coEvery { mockHandler.getState() } returns TestState(
            isEnabled = false,
            error = error
        )

        registry.components = setOf(mockComponent)

        val features = registry.getFeatures(FeatureCategory.Toggle)
        assertEquals(1, features.size)
        val feature = features[0]
        assertEquals(mockKey, feature.key)
        assertEquals(false, feature.state.isEnabled)
        assertEquals(error, feature.state.error)
    }

    @Test
    fun `when getting features by different category then returns empty list`() = runTest {
        coEvery { mockHandler.getState() } returns TestState(isEnabled = true)
        registry.components = setOf(mockComponent)
        val features = registry.getFeatures(FeatureCategory.ConfigurableToggle) // Different category
        assertTrue(features.isEmpty())
    }

    @Test
    fun `when multiple components exist then all are retrievable`() = runTest {
        // Setup second component
        val mockKey2 = object : FeatureKey<PolicyState> {
            override val featureName = "test_feature_2"
        }
        val mockHandler2 = mockk<FeatureHandler<PolicyState>>()
        val mockComponent2 = object : FeatureComponent<PolicyState> {
            override val featureName = "test_feature_2"
            override val title = "Test Feature 2"
            override val description = "some description 2"
            override val category = FeatureCategory.Toggle
            override val handler = mockHandler2
            override val defaultValue = TestState(isEnabled = false)
            override val key = mockKey2
        }

        coEvery { mockHandler.getState() } returns TestState(isEnabled = true)
        coEvery { mockHandler2.getState() } returns TestState(isEnabled = false)

        registry.components = setOf(mockComponent, mockComponent2)

        val features = registry.getFeatures(FeatureCategory.Toggle)
        assertEquals(2, features.size)
        assertTrue(features.any { it.key.featureName == "test_feature" && it.state.isEnabled })
        assertTrue(features.any { it.key.featureName == "test_feature_2" && !it.state.isEnabled })
    }

    @Test
    fun `when getting handler with different type then returns null`() {
        data class OtherState(
            override val isEnabled: Boolean,
            override val isSupported: Boolean = true,
            override val error: ApiError? = null,
            override val exception: Throwable? = null
        ) : PolicyState {
            override fun withEnabled(enabled: Boolean): PolicyState {
                return copy(isEnabled = enabled)
            }

            override fun withError(error: ApiError?, exception: Throwable?): PolicyState {
                return copy(error = error, exception = exception)
            }
        }

        val otherKey = object : FeatureKey<OtherState> { override val featureName = "test_feature" }
        registry.components = setOf(mockComponent)
        assertNull(registry.getHandler(otherKey))
    }

    @Test
    fun `when unsupported feature then returns state with isSupported false`() = runTest {
        coEvery { mockHandler.getState() } returns TestState(
            isEnabled = false,
            isSupported = false
        )

        registry.components = setOf(mockComponent)

        val features = registry.getFeatures(FeatureCategory.Toggle)
        assertEquals(1, features.size)
        assertEquals(false, features[0].state.isSupported)
    }

    @Test
    fun `when getting component then returns correct component`() {
        registry.components = setOf(mockComponent)
        val component = registry.getComponent(mockKey)
        assertEquals(mockComponent, component)
    }

    @Test
    fun `when getting non-existent component then returns null`() {
        registry.components = emptySet()
        val nonExistentKey = object : FeatureKey<TestState> {
            override val featureName = "non_existent"
        }
        assertNull(registry.getComponent(nonExistentKey))
    }
}