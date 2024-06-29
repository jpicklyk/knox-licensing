package net.sfelabs.core.domain.api.feature

import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.reflect.KClass

class FeatureHandlerFactoryTest {

    private lateinit var featureHandlerFactory: FeatureHandlerFactory

    private val handlers: Map<KClass<out FeatureKey<*>>, FeatureHandler<*>> = mapOf(
        TestFeatureKey.TestFeature1::class to TestFeatureHandler()
    )

    @Before
    fun setUp() {
        featureHandlerFactory = FeatureHandlerFactory(handlers)
    }

    @Test
    fun getHandler_shouldReturnCorrectHandlerForRegisteredFeature() {
        val handler = featureHandlerFactory.getHandler(TestFeatureKey.TestFeature1)
        assertTrue(handler is TestFeatureHandler)
    }

    @Test(expected = IllegalArgumentException::class)
    fun getHandler_shouldThrowExceptionForUnregisteredFeature() {
        featureHandlerFactory.getHandler(TestFeatureKey.TestFeature2)
    }
}