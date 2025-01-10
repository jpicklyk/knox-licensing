package net.sfelabs.knoxmoduleshowcase.tests.hdm

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.knox.android.AndroidApplicationContextProvider
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.use_cases.hdm.GetHdmPolicyUseCase
import net.sfelabs.knox_tactical.domain.use_cases.hdm.SetHdmBluetoothState
import net.sfelabs.knox_tactical.domain.use_cases.hdm.SetHdmCameraState
import net.sfelabs.knox_tactical.domain.use_cases.hdm.SetHdmExternalMemoryState
import net.sfelabs.knox_tactical.domain.use_cases.hdm.SetHdmGpsState
import net.sfelabs.knox_tactical.domain.use_cases.hdm.SetHdmMicrophoneState
import net.sfelabs.knox_tactical.domain.use_cases.hdm.SetHdmModemState
import net.sfelabs.knox_tactical.domain.use_cases.hdm.SetHdmNfcState
import net.sfelabs.knox_tactical.domain.use_cases.hdm.SetHdmPolicyUseCase
import net.sfelabs.knox_tactical.domain.use_cases.hdm.SetHdmSpeakerState
import net.sfelabs.knox_tactical.domain.use_cases.hdm.SetHdmUsbState
import net.sfelabs.knox_tactical.domain.use_cases.hdm.SetHdmWiFiState
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 131)
class HdmTests {
    private lateinit var context: Context
    private val cameraBitmask = 1
    private val mmcBitmask = 2
    private val usbBitmask = 4
    private val wifiBitmask = 8
    private val bluetoothBitmask = 16
    private val gpsBitmask = 32
    private val nfcBitmask = 64
    private val micBitmask = 128
    private val modemBitmask = 256
    private val speakerBitmask = 512

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        val testProvider = object: AndroidApplicationContextProvider {
            override fun getContext(): Context {
                return context
            }
        }
        AndroidApplicationContextProvider.init(testProvider)
    }

    @Test
    fun disableCamera() = runTest {
        val result = SetHdmCameraState().invoke(true)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && featureDisabled(currentPolicy.data, cameraBitmask ))
    }

    @Test
    fun enableCamera() = runTest {
        val result = SetHdmCameraState().invoke(false)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && !featureDisabled(currentPolicy.data, cameraBitmask))
    }

    @Test
    fun disableMmc() = runTest {
        val result = SetHdmExternalMemoryState().invoke(true)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && featureDisabled(currentPolicy.data, mmcBitmask ))
    }

    @Test
    fun enableMmc() = runTest {
        val result = SetHdmExternalMemoryState().invoke(false)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && !featureDisabled(currentPolicy.data, mmcBitmask ))
    }

    //@Test
    fun disableUsb() = runTest {
        val result = SetHdmUsbState().invoke(true)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && featureDisabled(currentPolicy.data, usbBitmask ))
    }

    @Test
    fun enableUsb() = runTest {
        val result = SetHdmUsbState().invoke(false)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && !featureDisabled(currentPolicy.data, usbBitmask ))
    }

    @Test
    fun disableWifi() = runTest {
        val result = SetHdmWiFiState().invoke(true)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && featureDisabled(currentPolicy.data, wifiBitmask ))
    }

    @Test
    fun enableWifi() = runTest {
        val result = SetHdmWiFiState().invoke(false)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && !featureDisabled(currentPolicy.data, wifiBitmask ))
    }

    @Test
    fun disableBluetooth() = runTest {
        val result = SetHdmBluetoothState().invoke(true)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && featureDisabled(currentPolicy.data, bluetoothBitmask ))
    }

    @Test
    fun enableBluetooth() = runTest {
        val result = SetHdmBluetoothState().invoke(false)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && !featureDisabled(currentPolicy.data, bluetoothBitmask ))
    }

    @Test
    fun disableGps() = runTest {
        val result = SetHdmGpsState().invoke(true)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        println("HDM Policy: $currentPolicy")
        assert(currentPolicy is ApiResult.Success && featureDisabled(currentPolicy.data, gpsBitmask ))
    }

    @Test
    fun enableGps() = runTest {
        val result = SetHdmGpsState().invoke(false)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && !featureDisabled(currentPolicy.data, gpsBitmask ))
    }

    @Test
    fun disableNfc() = runTest {
        val result = SetHdmNfcState().invoke(true)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && featureDisabled(currentPolicy.data, nfcBitmask ))
    }

    @Test
    fun enableNfc() = runTest {
        val result = SetHdmNfcState().invoke(false)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && !featureDisabled(currentPolicy.data, nfcBitmask ))
    }

    /**
     * The XCP HW does not correctly support this HDM control and will be a NO-OP
     */
    @Test
    @TacticalSdkSuppress(excludeModels = ["SM-G736U1"])
    fun disableMicrophone() = runTest {
        val result = SetHdmMicrophoneState().invoke(true)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && featureDisabled(currentPolicy.data, micBitmask ))
    }

    /**
     * The XCP HW does not correctly support this HDM control and will be a NO-OP
     */
    @Test
    @TacticalSdkSuppress(excludeModels = ["SM-G736U1"])
    fun enableMicrophone() = runTest {
        val result = SetHdmMicrophoneState().invoke(false)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && !featureDisabled(currentPolicy.data, micBitmask ))
    }

    @Test
    fun disableModem() = runTest {
        val result = SetHdmModemState().invoke(true)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && featureDisabled(currentPolicy.data, modemBitmask ))
    }

    @Test
    fun enableModem() = runTest {
        val result = SetHdmModemState().invoke(false)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && !featureDisabled(currentPolicy.data, modemBitmask ))
    }

    /**
     * The XCP HW does not correctly support this HDM control and will be a NO-OP
     */
    @Test
    @TacticalSdkSuppress(excludeModels = ["SM-G736U1"])
    fun disableSpeaker() = runTest {
        val result = SetHdmSpeakerState().invoke(true)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && featureDisabled(currentPolicy.data, speakerBitmask ))
    }

    /**
     * The XCP HW does not correctly support this HDM control and will be a NO-OP
     */
    @Test
    @TacticalSdkSuppress(excludeModels = ["SM-G736U1"])
    fun enableSpeaker() = runTest {
        val result = SetHdmSpeakerState().invoke(false)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && !featureDisabled(currentPolicy.data, speakerBitmask ))
    }

    @After
    fun resetHardware() = runTest {
        //Ensure that nothing is blocked
        SetHdmPolicyUseCase().invoke(0, false)
    }
    private fun featureDisabled(input: Int, bitmask: Int): Boolean {
        return input and bitmask != 0
    }
}