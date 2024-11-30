package net.sfelabs.knox_tactical.domain.feature

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.feature.domain.model.FeatureCategory
import net.sfelabs.core.knox.feature.domain.registry.DefaultFeatureRegistry
import net.sfelabs.knox_tactical.domain.api.tdm.GetTacticalDeviceModeUseCase
import net.sfelabs.knox_tactical.domain.api.tdm.SetTacticalDeviceModeUseCase
import net.sfelabs.knox_tactical.domain.api.tdm.generated.TacticalDeviceModeKey
import net.sfelabs.knox_tactical.domain.api.tdm.generated.TacticalDeviceModeRegistration
import org.junit.Before
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class TacticalDeviceModeIntegrationTest {
    private lateinit var registry: DefaultFeatureRegistry
    private lateinit var getUseCase: GetTacticalDeviceModeUseCase
    private lateinit var setUseCase: SetTacticalDeviceModeUseCase
    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext



        getUseCase = GetTacticalDeviceModeUseCase(context)
        setUseCase = SetTacticalDeviceModeUseCase(context)
        registry = DefaultFeatureRegistry()

        // Register feature
        registry.register(TacticalDeviceModeRegistration(getUseCase, setUseCase))
    }

    @Test
    @SmallTest
    fun integrationTestFeatureStateChangesAreReflectedInRegistry() = runTest {
        val key = TacticalDeviceModeKey()
        val handler = registry.getHandler(key)!!

        val state = handler.getState()
        assertTrue(state is ApiResult.Success)
        //assertTrue(state.data.value)
    }

    @Test
    @SmallTest
    fun integrationTestFeatureAppearsInCorrectCategory() = runTest {
        val features = registry.getFeatures(FeatureCategory.Toggle)
        assertEquals(1, features.size)
        assertTrue(features[0].key is TacticalDeviceModeKey)
    }
}