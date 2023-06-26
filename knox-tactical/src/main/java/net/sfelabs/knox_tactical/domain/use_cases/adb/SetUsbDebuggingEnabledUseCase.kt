package net.sfelabs.knox_tactical.domain.use_cases.adb

import android.util.Log
import com.samsung.android.knox.EnterpriseDeviceManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.core.ui.UnitApiCall
import net.sfelabs.core.ui.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class SetUsbDebuggingUseCase @Inject constructor(
    @TacticalSdk private val enterpriseDeviceManager: EnterpriseDeviceManager
) {

    suspend operator fun invoke(enable: Boolean): net.sfelabs.core.ui.UnitApiCall {
        val restrictionPolicy = enterpriseDeviceManager.restrictionPolicy
        return coroutineScope {
            try {
                val result = restrictionPolicy.setUsbDebuggingEnabled(enable)
                if (!result) {
                    net.sfelabs.core.ui.ApiCall.Error(
                        net.sfelabs.core.ui.UiText.DynamicString(
                            "setUsbDebuggingEnabled API error"
                        ))
                } else {
                    net.sfelabs.core.ui.ApiCall.Success(Unit)
                }

            } catch (se: SecurityException) {
                Log.e(null, se.message!!)
                net.sfelabs.core.ui.ApiCall.Error(
                    net.sfelabs.core.ui.UiText.DynamicString(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_RESTRICTION_MGMT\" permission"
                    ))
            }
        }
    }
}