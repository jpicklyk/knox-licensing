package net.sfelabs.knoxmoduleshowcase.usb

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.samsung.android.knox.AppIdentity
import com.samsung.android.knox.EnterpriseDeviceManager
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.knox_common.domain.use_cases.AllowUsbHostStorageUseCase
import net.sfelabs.knox_common.domain.use_cases.IsUsbHostStorageAllowedUseCase
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.use_cases.usb.AddPackageToUsbHostWhiteListUseCase
import net.sfelabs.knox_tactical.domain.use_cases.usb.GetPackagesFromUsbHostWhiteListUseCase
import net.sfelabs.knox_tactical.domain.use_cases.usb.RemovePackageFromUsbHostWhiteListUseCase
import net.sfelabs.knoxmoduleshowcase.app.getApplicationSignatures
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * 1. prepare 2 devices and 1 usb-c cable (a bit easier with a USB-A to USB-C with an OTG connector).
 * 2. connect 2 devices with usb-c cable.
 * 3. set 1 device(A) to call knox apis as host.
 * 4. check device A is able to read storage of the other device(B) by selecting "MTP Host" application.
 *    --> possible
 * 5. call allowUsbHostStorage(false) api to disable all usb host interface.
 * 6. check device A is able to read storage of the other device(B) by selecting "MTP Host" application.
 *   --> impossible
 * 7. call getPackagesFromUsbHostWhiteList(...) using "MTP Host" packagename - "com.samsung.android.mtp".
 * 8. check device A is able to read storage of the other device(B) by selecting "MTP Host" application.
 *   --> possible
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 100)
class UsbHostWhiteListTest {
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private val edm: EnterpriseDeviceManager = EnterpriseDeviceManager.getInstance(appContext)
    private val mtpPackage = "com.samsung.android.mtp"

    @Test
    fun step1_disableUsbHostMode() = runTest {
        val hostStorageUseCase = AllowUsbHostStorageUseCase(edm.restrictionPolicy)
        val res = hostStorageUseCase.invoke(false)
        assert(res is ApiCall.Success)

        val result = IsUsbHostStorageAllowedUseCase(edm.restrictionPolicy).invoke()
        assert(result is ApiCall.Success && !result.data)
    }

    @Test
    fun step2_addPackageToUsbWhitelist() = runTest {
        val useCase = AddPackageToUsbHostWhiteListUseCase(edm)

        val signatures = getApplicationSignatures(mtpPackage, appContext)
        val sig = signatures[0]
        println("Signature: $sig")
        val appIdentity = AppIdentity(mtpPackage, sig)
        val result = useCase.invoke(true, appIdentity)
        assert(result is ApiCall.Success)

        val useCase2 = GetPackagesFromUsbHostWhiteListUseCase(edm)
        val result2 =  useCase2.invoke()
        assert(result2 is ApiCall.Success && result2.data.contains(mtpPackage))

    }

    @Test
    fun step3_removePackageFromUsbWhiteList() = runTest {
        val useCase = RemovePackageFromUsbHostWhiteListUseCase(edm)

        val signatures = getApplicationSignatures(mtpPackage, appContext)
        val sig = signatures[0]
        println("Signature: $sig")
        val appIdentity = AppIdentity(mtpPackage, sig)
        val result = useCase.invoke(appIdentity)
        assert(result is ApiCall.Success)

        val useCase2 = GetPackagesFromUsbHostWhiteListUseCase(edm)
        val result2 =  useCase2.invoke()
        assert(result2 is ApiCall.Success && result2.data.isEmpty())
    }

    @Test
    fun step4_allowUsbHostTest() = runTest {
        val hostStorageUseCase = AllowUsbHostStorageUseCase(edm.restrictionPolicy)
        val res = hostStorageUseCase.invoke(true)
        assert(res is ApiCall.Success)

        val result = IsUsbHostStorageAllowedUseCase(edm.restrictionPolicy).invoke()
        assert(result is ApiCall.Success && result.data)
    }

}