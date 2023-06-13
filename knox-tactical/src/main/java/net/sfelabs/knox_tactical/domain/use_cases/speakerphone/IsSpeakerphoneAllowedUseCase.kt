package net.sfelabs.knox_tactical.domain.use_cases.speakerphone

import android.os.RemoteException
import com.samsung.android.knox.EnterpriseDeviceManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.common.core.ApiCall
import net.sfelabs.common.core.ui.UiText
import javax.inject.Inject

class IsSpeakerphoneAllowedUseCase @Inject constructor(
    private val enterpriseDeviceManager: EnterpriseDeviceManager
) {

    suspend operator fun invoke(): ApiCall<Boolean> {
        val restrictionPolicy = enterpriseDeviceManager.restrictionPolicy
        return coroutineScope {
            try {
                ApiCall.Success(restrictionPolicy.isSpeakerphoneAllowed)
            } catch (se: SecurityException) {
                ApiCall.Error(
                    UiText.DynamicString(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_RESTRICTION_MGMT\" permission"
                    ))
            } catch (re: RemoteException) {
                ApiCall.Error(UiText.DynamicString(re.message!!))
            } catch (nsme: NoSuchMethodError) {
                ApiCall.NotSupported
            }
        }
    }
}