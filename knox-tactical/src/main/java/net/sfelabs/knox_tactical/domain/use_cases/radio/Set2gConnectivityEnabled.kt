package net.sfelabs.knox_tactical.domain.use_cases.radio

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.UiText
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class Set2gConnectivityEnabled @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {

    suspend operator fun invoke(enabled: Boolean): UnitApiCall {
        return coroutineScope {
            try {
                when(systemManager.set2GConnectivityState(enabled)) {
                    CustomDeviceManager.SUCCESS -> ApiResult.Success(Unit)
                    else -> ApiResult.Error(UiText.DynamicString("The operation failed for an unknown reason."))
                }
            } catch (ex: NoSuchMethodError) {
                ApiResult.NotSupported
            }
        }
    }
}