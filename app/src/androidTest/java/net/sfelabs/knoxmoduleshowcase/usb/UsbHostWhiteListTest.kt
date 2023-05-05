package net.sfelabs.knoxmoduleshowcase.usb

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

/**
 * 1. prepare 2 devices and 1 usb-c cable.
 * 2. connect 2 devices with usb-c cable.
 * 3. set 1 device(A) to call knox apis as host.
 * 4. check device A is able to read storage of the other device(B) by selecting "MTP Host" application.
 *    --> possible
 * 5. call allowUsbHostStorage(false) api to disable all usb host interface.
 * 6. check device A is able to read storage of the other device(B) by selecting "MTP Host" application.
 *   --> impossible
 * 7. call getPackagesFromUsbHostWhiteList(...) using "MTP Host" packagename - "com.android.mtp".
 * 8. check device A is able to read storage of the other device(B) by selecting "MTP Host" application.
 *   --> possible
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class UsbHostWhiteListTest {
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private val edm: EnterpriseDeviceManager = EnterpriseDeviceManager.getInstance(appContext)

    @Test
    fun testAddPackageToUsbWhitelist() = runTest {
        val useCase =
            net.sfelabs.knox_tactical.domain.use_cases.usb.AddPackageToUsbWhiteListUseCase(
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