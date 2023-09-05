package net.sfelabs.knoxmoduleshowcase.android10

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.samsung.android.knox.EnterpriseDeviceManager
import com.samsung.android.knox.restriction.RestrictionPolicy
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.tdm.GetTacticalDeviceModeUseCase
import net.sfelabs.knox_tactical.domain.use_cases.tdm.SetTacticalDeviceModeUseCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TacticalDeviceModeTest {

    private lateinit var context: Context
    private lateinit var edm: EnterpriseDeviceManager
    private lateinit var restrictionPolicy: RestrictionPolicy

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().context
        edm = KnoxModule.provideKnoxEnterpriseDeviceManager(context)
        restrictionPolicy = KnoxModule.provideKnoxRestrictionPolicy(edm)
    }
    @Test
    fun enableTacticalDeviceMode() = runTest {
        val res1 = SetTacticalDeviceModeUseCase(restrictionPolicy).invoke(true)
        assert(res1 is ApiCall.Success)
        val res2 = GetTacticalDeviceModeUseCase(restrictionPolicy).invoke()
        assert(res2 is ApiCall.Success && res2.data.enabled)
    }

    @Test
    fun disableTacticalDeviceMode() = runTest {
        val res1 = SetTacticalDeviceModeUseCase(restrictionPolicy).invoke(false)
        assert(res1 is ApiCall.Success)
        val res2 = GetTacticalDeviceModeUseCase(restrictionPolicy).invoke()
        assert(res2 is ApiCall.Success && !res2.data.enabled)
    }

    //@After
    fun resetTacticalDeviceModeOff() = runTest {
        SetTacticalDeviceModeUseCase(restrictionPolicy).invoke(false)
    }
}