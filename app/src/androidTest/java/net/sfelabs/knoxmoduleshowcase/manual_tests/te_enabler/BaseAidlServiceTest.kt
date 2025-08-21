package net.sfelabs.knoxmoduleshowcase.manual_tests.te_enabler

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.IInterface
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert
import org.junit.Before

abstract class BaseAidlServiceTest<T> {
    protected val context: Context = ApplicationProvider.getApplicationContext()
    protected var serviceInterface: T? = null
    private var serviceConnection: ServiceConnection? = null
    private val connectionChannel = Channel<IBinder?>(1)

    companion object {
        protected const val SERVICE_CONNECTION_TIMEOUT = 10_000L
        protected const val AIDL_CALL_TIMEOUT = 5_000L
        protected const val SERVICE_PACKAGE = "com.partech.samservices"
    }

    // Abstract method each test class must implement
    protected abstract fun getServiceClassName(): String
    protected abstract fun createServiceInterface(binder: IBinder): T

    @Before
    fun setUp() = runBlocking {
        bindToService()
    }

    @After
    fun tearDown() {
        serviceConnection?.let { connection ->
            try {
                context.unbindService(connection)
            } catch (_: IllegalArgumentException) {
                // Service might already be unbound
            }
        }
        serviceInterface = null
        serviceConnection = null
    }

    protected fun testServiceConnection() = runBlocking {
        Assert.assertNotNull("AIDL service should be connected", serviceInterface)
        Assert.assertTrue(
            "Service should be alive",
            (serviceInterface as? IInterface)?.asBinder()?.isBinderAlive == true
        )
    }

    protected suspend fun <R> executeWithTimeout(block: suspend () -> R): R {
        return withTimeout(AIDL_CALL_TIMEOUT) {
            block()
        }
    }

    protected fun <T> createCallbackChannel(): Channel<T> = Channel(1)

    private suspend fun bindToService() {
        val serviceIntent = Intent().apply {
            component = ComponentName(SERVICE_PACKAGE, getServiceClassName())
        }

        serviceConnection = createServiceConnection()

        println("Attempting to bind to service: ${getServiceClassName()} in package: $SERVICE_PACKAGE")
        
        val bound = context.bindService(
            serviceIntent,
            serviceConnection!!,
            Context.BIND_AUTO_CREATE
        )

        if (!bound) {
            println("Component intent failed, trying explicit intent")
            // Try with explicit intent
            val explicitIntent = Intent().apply {
                setClassName(SERVICE_PACKAGE, getServiceClassName())
            }
            val boundExplicit = context.bindService(
                explicitIntent,
                serviceConnection!!,
                Context.BIND_AUTO_CREATE
            )
            
            if (!boundExplicit) {
                // Try with action-based intent
                println("Explicit intent failed, trying action-based intent")
                val actionIntent = Intent().apply {
                    action = "${SERVICE_PACKAGE}.${getServiceClassName().substringAfterLast('.')}"
                    `package` = SERVICE_PACKAGE
                }
                val boundAction = context.bindService(
                    actionIntent,
                    serviceConnection!!,
                    Context.BIND_AUTO_CREATE
                )
                Assert.assertTrue("Service should bind successfully (tried component, explicit, and action intents)", boundAction)
            }
        }

        val binder = withTimeout(SERVICE_CONNECTION_TIMEOUT) {
            connectionChannel.receive()
        }

        Assert.assertNotNull("Service binder should not be null", binder)
        serviceInterface = createServiceInterface(binder!!)
        Assert.assertNotNull("AIDL interface should not be null", serviceInterface)
    }

    private fun createServiceConnection() = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            connectionChannel.trySend(service)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceInterface = null
            connectionChannel.trySend(null)
        }
    }
}