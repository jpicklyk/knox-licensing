package net.sfelabs.knoxmoduleshowcase.manual_tests.te_enabler

import android.os.IBinder
import androidx.test.filters.LargeTest
import com.partech.samservices.IExecReceiverInterface
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

@LargeTest
class ReadMacFromTacticalEnablerService : BaseAidlServiceTest<IExecReceiverInterface>() {
    override fun getServiceClassName(): String = "com.partech.samservices.SamService"

    override fun createServiceInterface(binder: IBinder): IExecReceiverInterface {
        return IExecReceiverInterface.Stub.asInterface(binder)
    }

    @Test
    fun runServiceConnectionTest() = testServiceConnection()

    @Test
    fun testGetVersionCode() = runBlocking {
        val versionCode = executeWithTimeout {
            serviceInterface?.versionCode
        }

        assertNotNull("Version code should be returned", versionCode)
        assertTrue("Version code should be positive", versionCode!! > 0)
    }

    @Test
    fun testGetMacAddressForEthernet() = runBlocking {
        val macAddress = executeWithTimeout {
            serviceInterface?.getMacAddress("eth0")
        }

        assertNotNull("MAC address should be returned", macAddress)
        println("MAC address: $macAddress")
        // MAC address might be empty if interface doesn't exist - that's valid
    }
}