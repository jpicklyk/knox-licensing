package net.sfelabs.knox_ngd2.domain.use_cases.speakerphone

import android.os.RemoteException
import com.samsung.android.knox.EnterpriseDeviceManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.core.ui.UiText
import net.sfelabs.knox_ngd2.di.Ngd2Sdk
import javax.inject.Inject

@Deprecated("No longer supported as of Android T")
class IsSpeakerphoneAllowedUseCase @Inject constructor(
    @Ngd2Sdk private val enterpriseDeviceManager: EnterpriseDeviceManager
) {

    suspend operator fun invoke(): net.sfelabs.core.ui.ApiCall<Boolean> {
        val restrictionPolicy = enterpriseDeviceManager.restrictionPolicy
        return coroutineScope {
            try {
                net.sfelabs.core.ui.ApiCall.Success(restrictionPolicy.isSpeakerphoneAllowed)
            } catch (se: SecurityException) {
                net.sfelabs.core.ui.ApiCall.Error(
                    net.sfelabs.core.ui.UiText.DynamicString(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_RESTRICTION_MGMT\" permission"
                    ))
            } catch (re: RemoteException) {
                net.sfelabs.core.ui.ApiCall.Error(net.sfelabs.core.ui.UiText.DynamicString(re.message!!))
            } catch (nsme: NoSuchMethodError) {
                net.sfelabs.core.ui.ApiCall.NotSupported
            }
        }
    }
}