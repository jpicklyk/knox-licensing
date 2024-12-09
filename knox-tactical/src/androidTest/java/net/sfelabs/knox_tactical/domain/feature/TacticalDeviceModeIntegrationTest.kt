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
import net.sfelabs.core.knox.feature.hilt.HiltFeatureRegistry
import net.sfelabs.knox_tactical.domain.api.tdm.GetTacticalDeviceModeUseCase
import net.sfelabs.knox_tactical.domain.api.tdm.SetTacticalDeviceModeUseCase
import net.sfelabs.knox_tactical.domain.api.tdm.generated.TacticalDeviceModeComponent
import net.sfelabs.knox_tactical.domain.api.tdm.generated.TacticalDeviceModeKey
import org.junit.Before
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class TacticalDeviceModeIntegrationTest {
    private lateinit var registry: HiltFeatureRegistry
    private lateinit var getUseCase: GetTacticalDeviceModeUseCase
    private lateinit var setUseCase: SetTacticalDeviceModeUseCase
    private lateinit var component: TacticalDeviceModeComponent
    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext

        getUseCase = GetTacticalDeviceModeUseCase(context)
        setUseCase = SetTacticalDeviceModeUseCase(context)

        // Create component directly
        component = TacticalDeviceModeComponent(getUseCase, setUseCase)

        // Set up registry with component
        registry = HiltFeatureRegistry()
        registry.setComponents(setOf(component))
    }

    @Test
    @SmallTest
    fun integrationTestFeatureStateChangesAreReflectedInRegistry() = runTest {
        val handler = registry.getHandler(TacticalDeviceModeKey)!!

        val state = handler.getState()
        assertTrue(state is ApiResult.Success)
        //assertTrue(state.data.value)
    }

    @Test
    @SmallTest
    fun integrationTestFeatureAppearsInCorrectCategory() = runTest {
        val features = registry.getFeatures(FeatureCategory.Toggle)
        assertEquals(1, features.size)
        assertTrue(features[0].key === TacticalDeviceModeKey) // Using === since it's an object
    }
}