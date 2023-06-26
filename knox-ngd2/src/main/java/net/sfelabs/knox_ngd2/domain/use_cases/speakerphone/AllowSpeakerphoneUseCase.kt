package net.sfelabs.knox_ngd2.domain.use_cases.speakerphone

import com.samsung.android.knox.EnterpriseDeviceManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.core.ui.UnitApiCall
import net.sfelabs.core.ui.UiText
import net.sfelabs.knox_ngd2.di.Ngd2Sdk
import javax.inject.Inject

@Deprecated("No longer supported as of Android T")
class AllowSpeakerphoneUseCase @Inject constructor(
    @Ngd2Sdk private val enterpriseDeviceManager: EnterpriseDeviceManager
) {

    suspend operator fun invoke(allow: Boolean): net.sfelabs.core.ui.UnitApiCall {
        val restrictionPolicy = enterpriseDeviceManager.restrictionPolicy
        return coroutineScope {
            try {
                val result = restrictionPolicy.allowSpeakerphone(allow)
                if(result)
                    net.sfelabs.core.ui.ApiCall.Success(Unit)
                else
                    net.sfelabs.core.ui.ApiCall.Error(
                        net.sfelabs.core.ui.UiText.DynamicString("AllowSpeakerphone API was not not successful")
                    )
            } catch (se: SecurityException) {
                net.sfelabs.core.ui.ApiCall.Error(
                    net.sfelabs.core.ui.UiText.DynamicString(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_RESTRICTION_MGMT\" permission"
                    ))
            } catch (nsme: NoSuchMethodError) {
                net.sfelabs.core.ui.ApiCall.NotSupported
            }
        }
    }
}