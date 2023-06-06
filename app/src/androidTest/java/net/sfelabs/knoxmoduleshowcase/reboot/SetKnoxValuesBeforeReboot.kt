package net.sfelabs.knoxmoduleshowcase.reboot

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import net.sfelabs.common.core.ApiCall
import net.sfelabs.knox_tactical.domain.use_cases.wifi.GetWlan0MtuUseCase
import net.sfelabs.knox_tactical.domain.use_cases.wifi.SetWlan0MtuUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SetKnoxValuesBeforeReboot {
    private val sm = net.sfelabs.knox_tactical.di.KnoxModule.provideKnoxSystemManager()
    private val mtuValue = 1430
    @Test
    fun setWlanMtu() = runTest {
        val useCase = SetWlan0MtuUseCase(sm)
        val result = useCase.invoke(mtuValue)
        assert(result is ApiCall.Success)
    }

    @Test
    fun checkWlanMtu() = runTest {
        val useCase = GetWlan0MtuUseCase(sm)
        val result = useCase.invoke()
        assert(result is ApiCall.Success && result.data == mtuValue)
    }

    /*
    * There doesn't appear to be any way to run the adb shell commands from tests
    @Test
    fun checkWlanMtuFromAdb() = runTest {
        val result = Runtime.getRuntime().exec("shell ip address show wlan0")
        result.waitFor()
        val stdOut = result.inputStream.bufferedReader().use(BufferedReader::readText)
        val stdError = result.errorStream.bufferedReader().use(BufferedReader::readText)
        println(stdOut)
        println(stdError)
    }
     */

}
