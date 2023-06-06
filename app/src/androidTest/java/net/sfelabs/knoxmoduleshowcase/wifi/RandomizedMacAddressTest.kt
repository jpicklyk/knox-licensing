package net.sfelabs.knoxmoduleshowcase.wifi

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.samsung.android.knox.EnterpriseDeviceManager
import com.samsung.android.knox.restriction.RestrictionPolicy
import kotlinx.coroutines.test.runTest
import net.sfelabs.common.core.ApiCall
import net.sfelabs.knox_common.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.wifi.EnableRandomizedMacAddressUseCase
import net.sfelabs.knox_tactical.domain.use_cases.wifi.GetRandomizedMacAddressEnabledUseCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RandomizedMacAddressTest {
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
    fun testGetRandomizedMacAddressApi() = runTest {
        val useCase = GetRandomizedMacAddressEnabledUseCase(restrictionPolicy)
        val result = useCase.invoke()
        assert(result is ApiCall.Success)
    }

    @Test
    fun testDisableRandomizedMacAddress() = runTest {
        val useCase = EnableRandomizedMacAddressUseCase(restrictionPolicy)
        val result = useCase.invoke(false)
        assert(result is ApiCall.Success)
        val useCase2 = GetRandomizedMacAddressEnabledUseCase(restrictionPolicy)
        val result2 = useCase2.invoke()
        assert(result2 is ApiCall.Success && !result2.data )
    }

    @Test
    fun testEnableRandomizedMacAddress() = runTest {
        val useCase = EnableRandomizedMacAddressUseCase(restrictionPolicy)
        val result = useCase.invoke(true)
        assert(result is ApiCall.Success)
        val useCase2 = GetRandomizedMacAddressEnabledUseCase(restrictionPolicy)
        val result2 = useCase2.invoke()
        assert(result2 is ApiCall.Success && result2.data )
    }
}