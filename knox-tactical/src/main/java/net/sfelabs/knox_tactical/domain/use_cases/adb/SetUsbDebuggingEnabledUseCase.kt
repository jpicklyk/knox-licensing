package net.sfelabs.knox_tactical.domain.use_cases.adb

import android.util.Log
import com.samsung.android.knox.EnterpriseDeviceManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError
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
                    ApiResult.Error(
                        DefaultApiError.UnexpectedError(
                            "setUsbDebuggingEnabled API error"
                        )
                    )
                } else {
                    ApiResult.Success(Unit)
                }

            } catch (se: SecurityException) {
                Log.e(null, se.message!!)
                ApiResult.Error(
                    DefaultApiError.UnexpectedError(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_RESTRICTION_MGMT\" permission"
                    )
                )
            }
        }
    }
}