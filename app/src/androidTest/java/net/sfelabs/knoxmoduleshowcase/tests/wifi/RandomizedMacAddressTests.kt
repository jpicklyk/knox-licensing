package net.sfelabs.knoxmoduleshowcase.tests.wifi

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.samsung.android.knox.EnterpriseDeviceManager
import com.samsung.android.knox.restriction.RestrictionPolicy
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.annotations.ApiExists
import net.sfelabs.core.checkMethodExistence
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.wifi.EnableRandomizedMacAddressUseCase
import net.sfelabs.knox_tactical.domain.use_cases.wifi.GetRandomizedMacAddressEnabledUseCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 130)
class RandomizedMacAddressTests {
    private lateinit var context: Context
    private lateinit var edm: EnterpriseDeviceManager
    private lateinit var restrictionPolicy: RestrictionPolicy
    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        edm = KnoxModule.provideKnoxEnterpriseDeviceManager(context)
        restrictionPolicy = KnoxModule.provideKnoxRestrictionPolicy(edm)
    }

    @Test
    @ApiExists
    fun isRandomisedMacAddressEnabled_Exists() = runTest {
        assert(
            checkMethodExistence(
                restrictionPolicy::class,
                "isRandomisedMacAddressEnabled"
            )
        )
    }

    @Test
    @ApiExists
    fun enableRandomisedMacAddress_Exists() = runTest {
        assert(
            checkMethodExistence(
                restrictionPolicy::class,
                "enableRandomisedMacAddress"
            )
        )
    }

    @Test
    fun testGetRandomizedMacAddressApi() = runTest {
        val useCase = GetRandomizedMacAddressEnabledUseCase()
        val result = useCase.invoke()
        assert(result is ApiResult.Success)
    }

    @Test
    fun testDisableRandomizedMacAddress() = runTest {
        val useCase = EnableRandomizedMacAddressUseCase()
        val result = useCase.invoke(false)
        assert(result is ApiResult.Success)
        val useCase2 = GetRandomizedMacAddressEnabledUseCase()
        val result2 = useCase2.invoke()
        assert(result2 is ApiResult.Success && !result2.data )
    }

    @Test
    fun testEnableRandomizedMacAddress() = runTest {
        val useCase = EnableRandomizedMacAddressUseCase()
        val result = useCase.invoke(true)
        assert(result is ApiResult.Success)
        val useCase2 = GetRandomizedMacAddressEnabledUseCase()
        val result2 = useCase2.invoke()
        assert(result2 is ApiResult.Success && result2.data )
    }

    @After
    fun cleanup() = runTest {
        EnableRandomizedMacAddressUseCase().invoke(true)
    }
}