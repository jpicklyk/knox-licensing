package net.sfelabs.knox_tactical.domain.use_cases.hdm

import android.util.Base64
import com.samsung.android.knox.EnterpriseDeviceManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.core.ui.UiText
import java.util.UUID
import javax.inject.Inject

class GetHdmPolicyUseCase @Inject constructor(
    private val enterpriseDeviceManager: EnterpriseDeviceManager
) {

    suspend operator fun invoke(): ApiCall<String> {
        return coroutineScope {
            try {
                val hdmManager = enterpriseDeviceManager.hypervisorDeviceManager
                val response = hdmManager.getHdmPolicy(UUID.randomUUID().toString(), "stealth")
                ApiCall.Success(getPayload(response))
            } catch (e: Exception) {
                ApiCall.Error(UiText.DynamicString("getHdmPolicy failed: ${e.message}"))
            }
        }
    }

    private fun getPayload(hdmResponse: String?): String {
        if(hdmResponse == null)
            return "Null response"

        val body = hdmResponse.split(".")[1]
        return String(Base64.decode(body, Base64.DEFAULT or Base64.URL_SAFE))
    }
}
