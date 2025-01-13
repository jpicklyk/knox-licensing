package net.sfelabs.knoxmoduleshowcase.manual_tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.GetEthernetInterfaceNameForMacAddressUseCase
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.GetMacAddressForInterfaceUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@TacticalSdkSuppress(minReleaseVersion = 112)
class ReadMacFromInterfaceTest {
    private val systemManager = KnoxModule.provideKnoxSystemManager()

    /**
     * Note that this test requires that the device actually be connected to ethernet so make sure
     * to run test via paired wifi connection
     */
    @Test
    fun readHardwareAddress_eth0() = runBlocking {
        val name = "eth0"
        val useCase1 = GetMacAddressForInterfaceUseCase()
        val useCase2 = GetEthernetInterfaceNameForMacAddressUseCase(systemManager)
        val result = useCase1.invoke(name)
        assert(result is ApiResult.Success)
        if(result is ApiResult.Success) {
            val res2 = useCase2.invoke(result.data)
            assert(res2 is ApiResult.Success && res2.data == "eth0")
        }
    }

    @Test
    fun readHardwareAddress_br0() = runTest {
        val name = "br0"
        val result = GetMacAddressForInterfaceUseCase().invoke(name)
        assert(result is ApiResult.Success)
        if(result is ApiResult.Success) {
            println("MAC: ${result.data}")
        }
    }

    @Test
    fun readHardwareAddress_wlan0() = runTest {
        val name = "wlan0"
        val result = GetMacAddressForInterfaceUseCase().invoke(name)
        assert(result is ApiResult.Success)
        if(result is ApiResult.Success) {
            println("MAC: ${result.data}")
        }
    }

}