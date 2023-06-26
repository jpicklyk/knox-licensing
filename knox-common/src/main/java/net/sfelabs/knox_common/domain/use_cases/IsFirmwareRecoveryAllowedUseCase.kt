package net.sfelabs.knox_common.domain.use_cases

import com.samsung.android.knox.EnterpriseDeviceManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.core.ui.UiText
import javax.inject.Inject

class IsFirmwareRecoveryAllowedUseCase @Inject constructor(
    private val enterpriseDeviceManager: EnterpriseDeviceManager
) {

    suspend operator fun invoke(showMsg: Boolean = true): net.sfelabs.core.ui.ApiCall<Boolean> {
        val restrictionPolicy = enterpriseDeviceManager.restrictionPolicy
        return coroutineScope {
            try {
                net.sfelabs.core.ui.ApiCall.Success(restrictionPolicy.isFirmwareRecoveryAllowed(showMsg))
            } catch (se: SecurityException) {
                net.sfelabs.core.ui.ApiCall.Error(
                    net.sfelabs.core.ui.UiText.DynamicString(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_RESTRICTION_MGMT\" permission"
                    ))
            }
        }
    }
}