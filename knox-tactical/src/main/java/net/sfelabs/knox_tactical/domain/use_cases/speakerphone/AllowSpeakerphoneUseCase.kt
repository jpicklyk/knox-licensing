package net.sfelabs.knox_tactical.domain.use_cases.speakerphone

import android.os.RemoteException
import com.samsung.android.knox.EnterpriseDeviceManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.common.core.ApiCall
import net.sfelabs.common.core.UnitApiCall
import net.sfelabs.common.core.ui.UiText
import javax.inject.Inject

class AllowSpeakerphoneUseCase @Inject constructor(
    private val enterpriseDeviceManager: EnterpriseDeviceManager
) {

    suspend operator fun invoke(allow: Boolean): UnitApiCall {
        val restrictionPolicy = enterpriseDeviceManager.restrictionPolicy
        return coroutineScope {
            try {
                val result = restrictionPolicy.allowSpeakerphone(allow)
                if(result)
                    ApiCall.Success(Unit)
                else
                    ApiCall.Error(
                        UiText.DynamicString("AllowSpeakerphone API was not not successful")
                    )
            } catch (se: SecurityException) {
                ApiCall.Error(
                    UiText.DynamicString(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_RESTRICTION_MGMT\" permission"
                    ))
            } catch (nsme: NoSuchMethodError) {
                ApiCall.NotSupported
            }
        }
    }
}