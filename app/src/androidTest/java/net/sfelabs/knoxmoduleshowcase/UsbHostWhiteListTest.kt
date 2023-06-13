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

/**
 * 1. Prepare 2 devices and a USB-C cable
 * 2. Use the MTP Host application to validate that access from Device A to Device B is available
 * 3. Block USB Host Access {(}allowUsbHostStorage(false)}
 * 4. Validate that Device A is no longer able to read storage from Device B.
 * 4. Add whitelist for com.android.mtp
 * 5. Ensure Device A is able to read storage from Device B.
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
        val packageName = "com.android.mtp"
        //val packageName = "com.sec.android.app.myfiles"
        val signatures = getApplicationSignatures(packageName, appContext)
        val sig = signatures[0]
        println("Signature: $sig")
        val appIdentity = AppIdentity(packageName, sig)
        val result = useCase.invoke(enable = true, appIdentity)
        assert(result is ApiCall.Success)
    }


}