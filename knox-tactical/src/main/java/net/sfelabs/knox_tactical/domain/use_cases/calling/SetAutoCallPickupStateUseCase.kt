package net.sfelabs.knox_tactical.domain.use_cases.calling

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.domain.UiText
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
                        ApiResult.Success(Unit)
                    }
                    CustomDeviceManager.ERROR_INVALID_MODE_TYPE -> {
                        ApiResult.Error(UiText.DynamicString("Error Invalid Mode Type"))
                    }
                    else -> {
                        ApiResult.Error(UiText.DynamicString("Error not supported"))
                    }
                }

            } catch (e: SecurityException) {
                ApiResult.Error(
                    UiText.DynamicString(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_CUSTOM_SYSTEM\" permission"
                    ))
            } catch (nsm: NoSuchMethodError) {
                ApiResult.NotSupported
            }
        }
    }
}