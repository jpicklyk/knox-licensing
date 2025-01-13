package net.sfelabs.core.knox.feature

import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.model.DefaultApiError
import net.sfelabs.core.knox.feature.internal.handler.FeatureHandler
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.internal.component.FeatureComponent
import net.sfelabs.core.knox.feature.api.FeatureKey
import net.sfelabs.core.knox.feature.internal.model.FeatureState
import net.sfelabs.core.knox.feature.internal.registry.DefaultFeatureRegistry
import org.junit.Before
import org.junit.Test

class DefaultFeatureRegistryTest {
    private lateinit var registry: DefaultFeatureRegistry
    private lateinit var mockComponent: FeatureComponent<Boolean>
    private val mockKey = object : FeatureKey<Boolean> {
        override val featureName = "test_feature"
    }
    private val mockHandler = mockk<FeatureHandler<Boolean>>()

    @Before
    fun setup() {
        registry = DefaultFeatureRegistry()
        mockComponent = object : FeatureComponent<Boolean> {
            override val featureName = "test_feature"
            override val title = "Test Feature"
            override val description = "some description"
            override val category = FeatureCategory.Toggle
            override val handler = mockHandler
            override val defaultValue = false
            override val key = mockKey
        }
    }

    @Test
    fun `when getting features by category then only matching features are returned`() = runTest {
        coEvery { mockHandler.getState() } returns ApiResult.Success(FeatureState(true, true))

        registry.components = setOf(mockComponent)

        val features = registry.getFeatures(FeatureCategory.Toggle)
        assertEquals(1, features.size)
        assertEquals(mockKey, features[0].key)
    }

    @Test
    fun `when getting non-existent feature then returns null handler`() {
        registry.components = emptySet()
        assertNull(registry.getHandler(mockKey))
    }

    @Test
    fun `when feature state fetch fails then returns feature with default value`() = runTest {
        coEvery { mockHandler.getState() } returns ApiResult.Error(DefaultApiError.UnexpectedError())
        registry.components = setOf(mockComponent)

        val features = registry.getFeatures(FeatureCategory.Toggle)
        assertEquals(1, features.size)
        val feature = features[0]
        assertEquals(mockKey, feature.key)
        assertEquals(false, feature.state.enabled)
        assertEquals(false, feature.state.value)
    }

    @Test
    fun `when getting features by different category then returns empty list`() = runTest {
        registry.components = setOf(mockComponent)
        val features = registry.getFeatures(FeatureCategory.Toggle)
        assertTrue(features.isEmpty())
    }

    @Test
    fun `when multiple components exist then all are retrievable`() = runTest {
        // Setup second component
        val mockKey2 = object : FeatureKey<Boolean> { override val featureName = "test_feature_2" }
        val mockHandler2 = mockk<FeatureHandler<Boolean>>()
        val mockComponent2 = object : FeatureComponent<Boolean> {
            override val featureName = "test_feature_2"
            override val title = "Test Feature 2"
            override val description = "some description 2"
            override val category = FeatureCategory.Toggle
            override val handler = mockHandler2
            override val defaultValue = false
            override val key = mockKey2
        }

        coEvery { mockHandler.getState() } returns ApiResult.Success(FeatureState(true, true))
        coEvery { mockHandler2.getState() } returns ApiResult.Success(FeatureState(true, true))

        registry.components = setOf(mockComponent, mockComponent2)

        val features = registry.getFeatures(FeatureCategory.Toggle)
        assertEquals(2, features.size)
        assertTrue(features.any { it.key.featureName == "test_feature" })
        assertTrue(features.any { it.key.featureName == "test_feature_2" })
    }

    @Test
    fun `when getting handler with different type then returns null`() {
        val stringKey = object : FeatureKey<String> { override val featureName = "test_feature" }
        registry.components = setOf(mockComponent)
        assertNull(registry.getHandler(stringKey))
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
        val nonExistentKey = object : FeatureKey<Boolean> {
            override val featureName = "non_existent"
        }
        assertNull(registry.getComponent(nonExistentKey))
    }

    @Test
    fun `when multiple components exist then gets correct component by key`() {
        val mockKey2 = object : FeatureKey<Boolean> { override val featureName = "test_feature_2" }
        val mockHandler2 = mockk<FeatureHandler<Boolean>>()
        val mockComponent2 = object : FeatureComponent<Boolean> {
            override val featureName = "test_feature_2"
            override val title = "Test Feature 2"
            override val description = "some description 2"
            override val category = FeatureCategory.Toggle
            override val handler = mockHandler2
            override val defaultValue = false
            override val key = mockKey2
        }

        registry.components = setOf(mockComponent, mockComponent2)

        assertEquals(mockComponent, registry.getComponent(mockKey))
        assertEquals(mockComponent2, registry.getComponent(mockKey2))
    }
}