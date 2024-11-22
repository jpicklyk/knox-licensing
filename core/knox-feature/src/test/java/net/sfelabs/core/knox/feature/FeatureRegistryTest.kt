package net.sfelabs.core.knox.feature

import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError
import net.sfelabs.core.knox.feature.domain.handler.FeatureHandler
import net.sfelabs.core.knox.feature.domain.model.FeatureCategory
import net.sfelabs.core.knox.feature.domain.model.FeatureKey
import net.sfelabs.core.knox.feature.domain.model.FeatureState
import net.sfelabs.core.knox.feature.domain.registry.DefaultFeatureRegistry
import net.sfelabs.core.knox.feature.domain.registry.FeatureRegistration
import org.junit.Before
import org.junit.Test

class FeatureRegistryTest {
    private lateinit var registry: DefaultFeatureRegistry
    private lateinit var mockRegistration: FeatureRegistration<Boolean>
    private val mockKey = object : FeatureKey<Boolean> {
        override val featureName = "test_feature"
    }
    private val mockHandler = mockk<FeatureHandler<Boolean>>()

    @Before
    fun setup() {
        registry = DefaultFeatureRegistry()
        mockRegistration = object : FeatureRegistration<Boolean> {
            override val key = mockKey
            override val handler = mockHandler
            override val category = FeatureCategory.PRODUCTION
        }
    }

    @Test
    fun `when registering feature then it is retrievable`() {
        registry.register(mockRegistration)

        assertTrue(registry.isRegistered(mockKey))
        assertEquals(mockHandler, registry.getHandler(mockKey))
    }

    @Test
    fun `when getting features by category then only matching features are returned`() = runTest {
        coEvery { mockHandler.getState() } returns ApiResult.Success(FeatureState(true, true))

        registry.register(mockRegistration)

        val features = registry.getFeatures(FeatureCategory.PRODUCTION)
        assertEquals(1, features.size)
        assertEquals(mockKey, features[0].key)
    }

    @Test
    fun `when getting non-existent feature then returns null handler`() {
        assertNull(registry.getHandler(mockKey))
    }

    @Test
    fun `when feature state fetch fails then feature is filtered out`() = runTest {
        coEvery { mockHandler.getState() } returns ApiResult.Error(DefaultApiError.UnexpectedError())
        registry.register(mockRegistration)

        val features = registry.getFeatures(FeatureCategory.PRODUCTION)
        assertTrue(features.isEmpty())
    }

    @Test
    fun `when getting features by different category then returns empty list`() = runTest {
        registry.register(mockRegistration) // PRODUCTION category
        val features = registry.getFeatures(FeatureCategory.EXPERIMENTAL)
        assertTrue(features.isEmpty())
    }

    @Test
    fun `when registering multiple features then all are retrievable`() = runTest {
        // Setup second feature
        val mockKey2 = object : FeatureKey<Boolean> { override val featureName = "test_feature_2" }
        val mockHandler2 = mockk<FeatureHandler<Boolean>>()
        val mockRegistration2 = object : FeatureRegistration<Boolean> {
            override val key = mockKey2
            override val handler = mockHandler2
            override val category = FeatureCategory.PRODUCTION
        }

        coEvery { mockHandler.getState() } returns ApiResult.Success(FeatureState(true, true))
        coEvery { mockHandler2.getState() } returns ApiResult.Success(FeatureState(true, true))

        registry.register(mockRegistration)
        registry.register(mockRegistration2)

        assertEquals(2, registry.getFeatures(FeatureCategory.PRODUCTION).size)
    }

    @Test
    fun `when getting handler with different type then returns null`() {
        val stringKey = object : FeatureKey<String> { override val featureName = "test_feature" }
        registry.register(mockRegistration) // Boolean type
        assertNull(registry.getHandler(stringKey))
    }

    @Test
    fun `when registering feature with same name then overwrites previous registration`() = runTest {
        val mockHandler2 = mockk<FeatureHandler<Boolean>>()
        val mockRegistration2 = object : FeatureRegistration<Boolean> {
            override val key = mockKey // Same key as original mockRegistration
            override val handler = mockHandler2
            override val category = FeatureCategory.PRODUCTION
        }

        registry.register(mockRegistration)
        registry.register(mockRegistration2)

        assertEquals(mockHandler2, registry.getHandler(mockKey))
    }
}