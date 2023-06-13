package net.sfelabs.knoxmoduleshowcase

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import kotlinx.coroutines.test.runTest
import net.sfelabs.common.core.checkMethodExistence
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.speakerphone.IsSpeakerphoneAllowedUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class CheckAndroid13TacticalApisExist {
    private val systemManager = KnoxModule.provideKnoxSystemManager()
    private val settingsManager = KnoxModule.provideKnoxSettingsManager()
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private val edm = KnoxModule.provideKnoxEnterpriseDeviceManager(appContext)
    private val restrictionPolicy = KnoxModule.provideKnoxRestrictionPolicy(edm)
    private val applicationPolicy = KnoxModule.provideKnoxApplicationPolicy(edm)

    /** RAM Plus Related APIs **/
    @Test
    fun getRamPlusDisableState_Exists() = runTest {
        val kClass = systemManager::class
        assert(checkMethodExistence(kClass, "getRamPlusDisableState"))
    }

    @Test
    fun setRamPlusDisableState_Exists() = runTest {
        val kClass = systemManager::class
        assert(checkMethodExistence(kClass, "setRamPlusDisableState"))
    }

    /** USB Related APIs **/
    @Test
    fun setUsbDeviceAccessAllowedListSerialNumber_Exists() = runTest {
        assert(
            checkMethodExistence(
                systemManager::class,
                "setUsbDeviceAccessAllowedListSerialNumber"
            )
        )
    }

    @Test
    fun getUsbDeviceAccessAllowedListSerialNumber_Exists() = runTest {
        assert(
            checkMethodExistence(
                systemManager::class,
                "getUsbDeviceAccessAllowedListSerialNumber"
            )
        )
    }

    /** Wi-Fi Related APIs **/
    @Test
    fun isRandomisedMacAddressEnabled_Exists() = runTest {
        assert(
            checkMethodExistence(
                restrictionPolicy::class,
                "isRandomisedMacAddressEnabled"
            )
        )
    }

    @Test
    fun enableRandomisedMacAddress_Exists() = runTest {
        assert(
            checkMethodExistence(
                restrictionPolicy::class,
                "enableRandomisedMacAddress"
            )
        )
    }

    /** Lockscreen timeout APIs **/
    @Test
    fun setActivityTime_Exists() = runTest {
        assert(
            checkMethodExistence(
                systemManager::class,
                "setActivityTime"
            )
        )
    }

    @Test
    fun getActivityTime_Exists() = runTest {
        assert(
            checkMethodExistence(
                systemManager::class,
                "getActivityTime"
            )
        )
    }

    /**
     * Speakerphone Related APIs
     * XC6P does not support these APIs so failures here are acceptable.
     */
    @Test
    fun isSpeakerphoneAllowedUseCase_Exists() = runTest {
        if(Build.PRODUCT != "xcoverpro2ue") {
            assert(
                checkMethodExistence(
                    restrictionPolicy::class,
                    "isSpeakerphoneAllowed"
                )
            )
        } else {
            println("Skipping test for device: ${Build.PRODUCT}")
        }
    }

    @Test
    fun allowSpeakerphone_Exists() = runTest {
        if(Build.PRODUCT != "xcoverpro2ue") {
            assert(
                checkMethodExistence(
                    restrictionPolicy::class,
                    "allowSpeakerphoneUseCase"
                )
            )
        } else {
            println("Skipping test for device: ${Build.PRODUCT}")
        }
    }
}