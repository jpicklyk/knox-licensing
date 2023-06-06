package net.sfelabs.knox_tactical.domain.use_cases.calling

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.common.core.ApiCall
import net.sfelabs.common.core.UnitApiCall
import net.sfelabs.common.core.ui.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import net.sfelabs.knox_tactical.domain.model.AutoCallPickupState
import javax.inject.Inject

/**
 * This Knox API isn't TE specific but the flag ENABLED_ALWAYS_ACCEPT is.
 */
class SetAutoCallPickupStateUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
){
    suspend operator fun invoke(state: AutoCallPickupState): UnitApiCall {
        return coroutineScope {
            try {
                when (systemManager.setAutoCallPickupState(state.value)) {
                    CustomDeviceManager.SUCCESS -> {
                        ApiCall.Success(Unit)
                    }
                    CustomDeviceManager.ERROR_INVALID_MODE_TYPE -> {
                        ApiCall.Error(UiText.DynamicString("Error Invalid Mode Type"))
                    }
                    else -> {
                        ApiCall.Error(UiText.DynamicString("Error not supported"))
                    }
                }

            } catch (e: SecurityException) {
                ApiCall.Error(
                    UiText.DynamicString(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_CUSTOM_SYSTEM\" permission"
                    ))
            } catch (nsm: NoSuchMethodError) {
                ApiCall.NotSupported
            }
        }
    }
}