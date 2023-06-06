package net.sfelabs.knoxmoduleshowcase.usb

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.samsung.android.knox.AppIdentity
import com.samsung.android.knox.EnterpriseDeviceManager
import kotlinx.coroutines.test.runTest
import net.sfelabs.common.core.ApiCall
import net.sfelabs.common.core.signing.getApplicationSignatures
import net.sfelabs.knox_common.AllowUsbHostStorageUseCase
import net.sfelabs.knox_tactical.domain.use_cases.usb.AddPackageToUsbWhiteListUseCase
import net.sfelabs.knox_tactical.domain.use_cases.usb.GetPackagesFromUsbHostWhiteListUseCase
import org.junit.After
import org.junit.Before
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
@RunWith(AndroidJUnit4::class)
class UsbHostWhiteListTest {
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private val edm: EnterpriseDeviceManager = EnterpriseDeviceManager.getInstance(appContext)


    @Before
    fun disableUsbHostMode()= runTest {
        val hostStorageUseCase = AllowUsbHostStorageUseCase(edm.restrictionPolicy)
        val res = hostStorageUseCase.invoke(false)
        assert(res is ApiCall.Success)
    }

    @Test
    fun addPackageToUsbWhitelist() = runTest {
        val useCase = AddPackageToUsbWhiteListUseCase(edm)
        val packageName = "com.android.mtp"
        val signatures = getApplicationSignatures(packageName, appContext)
        val sig = signatures[0]
        println("Signature: $sig")
        val appIdentity = AppIdentity(packageName, sig)
        val result = useCase.invoke(appIdentity)
        assert(result is ApiCall.Success)

        val useCase2 = GetPackagesFromUsbHostWhiteListUseCase(edm)
        val result2 =  useCase2.invoke()
        assert(result2 is ApiCall.Success && result2.data.contains("com.android.mtp"))

    }



    @After
    fun allowUsbHostTest() = runTest {
        val hostStorageUseCase = AllowUsbHostStorageUseCase(edm.restrictionPolicy)
        val res = hostStorageUseCase.invoke(true)
    }
}