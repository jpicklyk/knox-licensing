package net.sfelabs.knoxmoduleshowcase.manual_tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.model.AdbHeader
import net.sfelabs.knox_tactical.domain.use_cases.adb.ExecuteAdbCommandUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@TacticalSdkSuppress(minReleaseVersion = 141)
class VlanTests {


    @Test
    fun configureEthernetVlan() = runBlocking {
        val ipAddress = "10.0.2.3"


        val vlanCreationCommand = "link add link eth0 name eth0.2 vlan id 2"
        val upVlanInterfaceCommand = "link set dev eth0.2 up"

        val commands = listOf(
            vlanCreationCommand,
            upVlanInterfaceCommand
        )
        var result = true
        for(command in commands) {
            val apiResult = ExecuteAdbCommandUseCase().invoke(AdbHeader.IP, command)
            result = if(apiResult is ApiResult.Success)
                result and true
            else
                false
            Thread.sleep(500)
        }
        assert(result)
    }
}