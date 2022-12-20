package net.sfelabs.knox_tactical.domain.use_cases.tactical.adb

import android.util.Log
import com.samsung.android.knox.EnterpriseDeviceManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.common.core.ApiCall
import net.sfelabs.common.core.UnitApiCall
import net.sfelabs.common.core.ui.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class SetUsbDebuggingUseCase @Inject constructor(
    @TacticalSdk private val enterpriseDeviceManager: EnterpriseDeviceManager
) {

    suspend operator fun invoke(enable: Boolean): UnitApiCall {
        val restrictionPolicy = enterpriseDeviceManager.restrictionPolicy
        return coroutineScope {
            try {
                val result = restrictionPolicy.setUsbDebuggingEnabled(enable)
                if (!result) {
                    ApiCall.Error(
                        UiText.DynamicString(
                            "setUsbDebuggingEnabled API error"
                        ))
                } else {
                    ApiCall.Success(Unit)
                }

            } catch (se: SecurityException) {
                Log.e(null, se.message!!)
                ApiCall.Error(
                    UiText.DynamicString(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_RESTRICTION_MGMT\" permission"
                    ))
            }
        }
    }
}