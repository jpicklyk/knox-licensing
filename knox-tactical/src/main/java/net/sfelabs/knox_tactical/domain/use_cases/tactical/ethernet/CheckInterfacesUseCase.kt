package net.sfelabs.knox_tactical.domain.use_cases.tactical.ethernet

import kotlinx.coroutines.coroutineScope
import net.sfelabs.android_log_wrapper.Log
import net.sfelabs.common.core.ApiCall
import net.sfelabs.common.core.ui.UiText
import net.sfelabs.common.core.UnitApiCall
import java.net.NetworkInterface
import javax.inject.Inject

class CheckInterfacesUseCase @Inject constructor(
    private val log: Log
) {

    suspend operator fun invoke(): UnitApiCall {
        return coroutineScope {
            try {
                val interfaces = NetworkInterface.getNetworkInterfaces()
                if(interfaces == null) {
                    log.e("Unable to retrieve any network interfaces")
                    ApiCall.Error(UiText.DynamicString("Unable to retrieve any network interfaces"))
                } else {
                    var count: Int = 0
                    while (interfaces.hasMoreElements()) {
                        val ne = interfaces.nextElement()
                        if (ne.displayName.startsWith("eth")) {
                            count++
                            val message = StringBuilder("------------------\n")
                            message.append("Checking interface: ${ne.displayName}\n")
                            message.append("Is up? ${ ne.isUp }\n")
                            message.append("IP addresses assigned to this interface are:\n")
                            val addresses = ne.inetAddresses
                            while (addresses.hasMoreElements()) {
                                message.append(addresses.nextElement().toString()+"\n")
                            }
                            message.append("MAC Address: "+getEthernetMacAddress(ne.hardwareAddress)+"/n")
                            message.append("------------------")
                            log.d(message = message.toString())
                        }
                    }
                    if(count == 0) {
                        log.d("No ethernet interfaces connected")
                        ApiCall.Error(UiText.DynamicString("No ethernet interfaces connected"))
                    }
                    ApiCall.Success(Unit)
                }
            } catch (e: Exception) {
                e.message?.let { log.e(message = it, e) }
                ApiCall.Error(
                    UiText.DynamicString(
                    e.message?: "No ethernet interfaces connected"
                ))
            }
        }
    }

    private fun getEthernetMacAddress(macBytes: ByteArray?): String? {
        return try {
            val macStringBuilder = StringBuilder()
            if (macBytes != null) {
                for (byte in macBytes) {
                    macStringBuilder.append(String.format("%02X:", byte))
                }
            }
            if (macStringBuilder.isNotEmpty()) {
                macStringBuilder.deleteCharAt(macStringBuilder.length - 1)
            }
            macStringBuilder.toString()
        } catch (e: Exception) {
            null
        }
    }


}