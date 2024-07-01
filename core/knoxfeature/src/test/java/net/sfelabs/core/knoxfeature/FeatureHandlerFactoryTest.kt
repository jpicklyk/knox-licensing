package net.sfelabs.core.knoxfeature

import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.knoxfeature.domain.FeatureHandler
import net.sfelabs.core.knoxfeature.domain.FeatureHandlerFactory
import net.sfelabs.core.knoxfeature.model.FeatureState
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.reflect.KClass

class FeatureHandlerFactoryTest {

    private lateinit var featureHandlerFactory: FeatureHandlerFactory

    private val handlers: Map<KClass<out Any>, Any> = mapOf(
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

    // Define test classes and objects
    sealed class TestFeatureKey : FeatureKey<Boolean> {
        object TestFeature1 : TestFeatureKey() {
            override val featureName: String = "test_feature_1"
        }
        object TestFeature2 : TestFeatureKey() {
            override val featureName: String = "test_feature_2"
        }
    }

    class TestFeatureHandler : FeatureHandler<Boolean> {
        override suspend fun getState(): ApiResult<FeatureState<Boolean>> {
            // Implement for test
            return ApiResult.Success(FeatureState(true, true))
        }

        override suspend fun setState(newState: FeatureState<Boolean>): ApiResult<Unit> {
            // Implement for test
            return ApiResult.Success(Unit)
        }
    }
}