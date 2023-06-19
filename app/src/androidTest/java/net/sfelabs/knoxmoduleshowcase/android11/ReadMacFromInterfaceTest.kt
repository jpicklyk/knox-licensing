package net.sfelabs.knoxmoduleshowcase.android11

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import net.sfelabs.common.core.ApiCall
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.GetEthernetInterfaceNameForMacAddressUseCase
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.GetMacAddressForInterfaceUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReadMacFromInterfaceTest {
    private val systemManager = KnoxModule.provideKnoxSystemManager()

    /**
     * Note that this test requires that the device actually be connected to ethernet so make sure
     * to run test via paired wifi connection
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