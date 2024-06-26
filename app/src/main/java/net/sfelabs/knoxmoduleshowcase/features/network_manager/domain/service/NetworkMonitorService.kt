package net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.ConnectivityManager
import android.net.EthernetNetworkSpecifier
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ServiceCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import net.sfelabs.knoxmoduleshowcase.R
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.Interface
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.NetworkInterfaceState
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.toInterface
import java.net.NetworkInterface
import javax.inject.Inject


@AndroidEntryPoint
class NetworkMonitorService @Inject constructor() : Service() {
    private val tag = "NetworkMonitorService"
    @Inject
    lateinit var connectivityManager: ConnectivityManager


    private val serviceScope = CoroutineScope(Dispatchers.Default)

    private val networkCallbacks = mutableMapOf<String, ConnectivityManager.NetworkCallback>()

    /* If I want to allow service connections from other apps I can create a binder like so:
    */
    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): NetworkMonitorService = this@NetworkMonitorService
    }
    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterNetworkCallbacks()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_START_FOREGROUND_SERVICE) {
            val interfaceNames: ArrayList<String> =
                intent.getStringArrayListExtra("interfaces") ?: arrayListOf()
            monitorInterfaces(interfaceNames)
            startForegroundService(interfaceNames)
        }
        return START_STICKY
    }

    fun monitorInterface(interfaceName: String) {
        setupNetworkCallback(interfaceName)
        requestNetworkCallback(interfaceName, networkCallbacks[interfaceName]!!)
    }

    private fun startForegroundService(interfaceNames: ArrayList<String>) {
        val notification = createNotification(this, interfaceNames)
        try {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notification,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                } else {
                    0
                }
            )
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun monitorInterfaces(interfaceNames: ArrayList<String>) {
        unregisterNetworkCallbacks()
        Log.d(tag, "Setting up network callbacks")
        interfaceNames
            //filter only interface names that are handled by ConnectivityManager
            .filter {  it.matches(Regex("^(wlan|eth|usb)[0-8]", RegexOption.IGNORE_CASE))  }
            .forEach { interfaceName ->
                setupNetworkCallback(interfaceName)
            }

        Log.d(tag, "Registering network callbacks: ${networkCallbacks.keys}")
        networkCallbacks.forEach { (interfaceName, callback) ->
            requestNetworkCallback(interfaceName, callback)
        }
    }

    private fun setupNetworkCallback(interfaceName: String) {
        Log.d(tag, "Interface $interfaceName passed filter")
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Log.d(tag, "Interface [${network.networkHandle}] $interfaceName is connected")

                serviceScope.launch {
                    _networkStateFlow.emit(
                        Pair(interfaceName, getInterfaceByName(interfaceName, true))
                    )
                }
            }

            override fun onLinkPropertiesChanged(
                network: Network,
                linkProperties: LinkProperties
            ) {
                Log.d(tag, "LinkPropertiesChanged [${network.networkHandle}] ${linkProperties.interfaceName}")
                super.onLinkPropertiesChanged(network, linkProperties)
            }

            override fun onLost(network: Network) {
                Log.d(tag, "Interface [${network.networkHandle}] $interfaceName is disconnected")

                serviceScope.launch {
                    _networkStateFlow.emit(
                        Pair(interfaceName, getInterfaceByName(interfaceName, false))
                    )
                }
            }
        }
        networkCallbacks[interfaceName] = networkCallback
    }

    private fun getInterfaceByName(interfaceName: String, isConnected: Boolean? = null): Interface {
        val networkInterface = NetworkInterface.getByName(interfaceName)
            ?: return Interface(
                interfaceName,
                interfaceAddresses = emptyList(),
                macAddress = null,
                networkStatus = when (isConnected) {
                    true -> NetworkInterfaceState.Connected
                    false -> NetworkInterfaceState.Disconnected
                    null -> NetworkInterfaceState.Unknown
                }
            )
        return networkInterface.toInterface(isConnected)
    }

    private fun requestNetworkCallback(interfaceName: String, callback: ConnectivityManager.NetworkCallback) {
        val builder = NetworkRequest.Builder()
        when {
            interfaceName.startsWith("wlan") ->
                builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)

            interfaceName.startsWith("eth")  ->
                builder.addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)

            interfaceName.startsWith("usb")
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                builder.addTransportType(NetworkCapabilities.TRANSPORT_USB)
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            setNetworkSpecifier(interfaceName, builder)
        } else {
            builder.setNetworkSpecifier(interfaceName)
        }

        val networkRequest = builder.build()

        connectivityManager.requestNetwork(networkRequest, callback)

    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun setNetworkSpecifier(interfaceName: String, builder: NetworkRequest.Builder) {
        when {
            interfaceName.startsWith("eth") ->
                builder.setNetworkSpecifier(EthernetNetworkSpecifier(interfaceName))
            //Fall back to the deprecated method as there is nothing better to do

        }
    }


    private fun unregisterNetworkCallbacks() {
        Log.d(tag, "Unregistering network callbacks: ${networkCallbacks.keys}")
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallbacks.forEach { (_, callback) ->
            connectivityManager.unregisterNetworkCallback(callback)
        }
        networkCallbacks.clear()
    }


    companion object {
        private const val NOTIFICATION_ID = 1
        private const val ACTION_START_FOREGROUND_SERVICE =
            "networkmonitorservice.action.startforeground"
        private val _networkStateFlow = MutableSharedFlow<Pair<String, Interface>>()

        fun startService(context: Context, interfaceNames: ArrayList<String>) {
            val intent = Intent(context, NetworkMonitorService::class.java)
                .putStringArrayListExtra("interfaces", interfaceNames)
                .setAction(ACTION_START_FOREGROUND_SERVICE)
            context.startForegroundService(intent)
        }

        fun getNetworkStateFlow() = _networkStateFlow.asSharedFlow()

        private fun createNotification(context: Context, interfaceNames: List<String>): Notification {
            val channelId = "network_monitoring_channel"
            val channelName = "Network Monitoring"
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)

            val notificationBuilder = Notification.Builder(context, channelId)
                .setContentTitle("Network Monitoring Service")
                .setContentText("Monitoring interfaces: ${interfaceNames.joinToString(", ")}")
                /*
                Starting in Android 13 (API level 33), users can dismiss the notification associated
                with a foreground service by default. To do so, users perform a swipe gesture on the
                notification. Traditionally, the notification isn't dismissed unless the foreground
                service is either stopped or removed from the foreground.

                If you want the notification non-dismissable by the user, pass true into the
                setOngoing() method when you create your notification
                 */
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_default_notification)

            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)

            return notificationBuilder.build()
        }

        fun stopService(context: Context) {
            val intent = Intent(context, NetworkMonitorService::class.java)
            context.stopService(intent)
        }
    }

}