package net.sfelabs.knoxmoduleshowcase

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.samsung.android.knox.AppIdentity
import com.samsung.android.knox.EnterpriseDeviceManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import net.sfelabs.common.core.ApiCall
import net.sfelabs.common.core.signing.getApplicationSignatures
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class UsbHostWhiteListTest {
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private val edm: EnterpriseDeviceManager = EnterpriseDeviceManager.getInstance(appContext)

    @Test
    fun testAddPackageToUsbWhitelist() = runTest {
        val useCase =
            net.sfelabs.knox_tactical.domain.use_cases.tactical.usb.AddPackageToUsbWhiteListUseCase(
                edm
            )
        val packageName = "com.android.chrome"
        val signatures = getApplicationSignatures(packageName, appContext)
        val sig = signatures[0]
        println("Signature: $sig")
        val appIdentity = AppIdentity(packageName, sig)
        val result = useCase.invoke(appIdentity)
        assert(result is ApiCall.Success)
    }


}