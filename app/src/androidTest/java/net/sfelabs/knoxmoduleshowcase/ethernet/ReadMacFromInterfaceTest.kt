package net.sfelabs.knoxmoduleshowcase.ethernet

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import net.sfelabs.common.core.ApiCall
import net.sfelabs.common.core.checkMethodExistence
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.GetEthernetInterfaceNameForMacAddressUseCase
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.GetMacAddressForInterfaceUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReadMacFromInterfaceTest {
    private val systemManager = KnoxModule.provideKnoxSystemManager()

    @Test
    fun getMacAddressForEthernetInterface_Exists() = runTest {
        assert(checkMethodExistence(systemManager::class,"getMacAddressForEthernetInterface"))
    }

    @Test
    fun getEthernetInterfaceNameForMacAddress_Exists() = runTest {
        assert(checkMethodExistence(systemManager::class,"getEthernetInterfaceNameForMacAddress"))
    }

    /**
     * Note that this test requires that the device actually be connected to ethernet
     */
    @Test
    fun readHardwareAddress() = runBlocking {
        val name = "eth0"
        val useCase1 = GetMacAddressForInterfaceUseCase(systemManager)
        val useCase2 = GetEthernetInterfaceNameForMacAddressUseCase(systemManager)
        val result = useCase1.invoke(name)
        assert(result is ApiCall.Success)
        if(result is ApiCall.Success) {
            val res2 = useCase2.invoke(result.data)
            assert(res2 is ApiCall.Success && res2.data == "eth0")
        }
    }
}