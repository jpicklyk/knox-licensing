package net.sfelabs.knoxmoduleshowcase

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.knoxmoduleshowcase.app.checkMethodExistence
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knoxmoduleshowcase.annotations.ApiExists
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
@ApiExists
class CheckAndroid11TacticalApisExist {
    private val systemManager = KnoxModule.provideKnoxSystemManager()

    /** Ethernet APIs **/
    @Test
    fun getMacAddressForEthernetInterface_Exists() = runTest {
        assert(checkMethodExistence(systemManager::class,"getMacAddressForEthernetInterface"))
    }
    @Test
    fun getEthernetInterfaceNameForMacAddress_Exists() = runTest {
        assert(checkMethodExistence(systemManager::class,"getEthernetInterfaceNameForMacAddress"))
    }

    /** PPPD Related APIs **/
    @Test
    fun stopPppd_Exists() = runTest {
        assert(checkMethodExistence(systemManager::class,"stopPPPD"))
    }

    /** Phone Related APIs **/
    @Test
    fun getAutoCallPickupState_Exists() = runTest {
        val kClass = systemManager::class
        assert(checkMethodExistence(kClass, "getAutoCallPickupState"))
    }

    @Test
    fun setAutoCallPickupState_Exists() = runTest {
        val kClass = systemManager::class
        assert(checkMethodExistence(kClass, "setAutoCallPickupState"))
    }

    @Test
    fun getAutoRecordCallEnabledState_Exists() = runTest {
        val kClass = systemManager::class
        assert(checkMethodExistence(kClass, "getAutomaticRecordCallEnabledState"))
    }

    @Test
    fun setAutoRecordCallEnabledState_Exists() = runTest {
        val kClass = systemManager::class
        assert(checkMethodExistence(kClass, "setAutomaticRecordCallEnabledState"))
    }

    /** PPPD Related APIs **/
    @Test
    fun executeAdbCommand_Exists() = runTest {
        assert(checkMethodExistence(systemManager::class,"executeAdbCommand"))
    }
}