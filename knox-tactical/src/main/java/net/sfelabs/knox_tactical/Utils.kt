@file:Suppress("UNUSED_EXPRESSION")

package net.sfelabs.knox_tactical

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import com.samsung.android.knox.custom.CustomDeviceManager




@RequiresApi(Build.VERSION_CODES.S)
fun registerNetwork(
    context: Context, interfaceName: String, onAvailable: (Any) -> Unit, onLost: (Any) -> Unit
) {
    val networkCallback = object: NetworkCallback(){

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            onAvailable
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            onLost
        }
    }
    registerNetwork(context, interfaceName, networkCallback)
}

@Suppress("DEPRECATION")
fun registerNetwork(context: Context, interfaceName: String, networkCallback: NetworkCallback) {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkRequest = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
        .setNetworkSpecifier(interfaceName)
        .build()
    connectivityManager.requestNetwork(networkRequest, networkCallback)
}

fun Boolean.toOnOrOff(): Int {
    return when(this) {
        true -> CustomDeviceManager.ON
        false -> CustomDeviceManager.OFF
    }
}