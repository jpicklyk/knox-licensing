package net.sfelabs.knox_tactical.domain.use_cases.hotspot

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SettingsManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError
import net.sfelabs.core.knox.feature.domain.model.FeatureState
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class GetHotspot20StateUseCase @Inject constructor(
        @TacticalSdk private val settingsManager: SettingsManager
    ) {
        suspend operator fun invoke(): ApiResult<FeatureState<Int>> {
            return coroutineScope {
                try {
                    when(val result = settingsManager.hotspot20State) {
                        CustomDeviceManager.ON -> ApiResult.Success(FeatureState(true, result))
                        CustomDeviceManager.OFF -> ApiResult.Success(FeatureState(false, result))
                        else -> ApiResult.Error(
                            DefaultApiError.UnexpectedError(
                                "Unexpected value returned: $result"
                            )
                        )
                    }
                } catch (e: SecurityException) {
                    ApiResult.Error(
                        DefaultApiError.UnexpectedError(
                            "The use of this API requires the caller to have the " +
                                    "\"com.samsung.android.knox.permission.KNOX_CUSTOM_SETTING\" permission"
                        )
                    )
                } catch (nsm: NoSuchMethodError) {
                    ApiResult.NotSupported
                }
            }
        }
}

/*
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private suspend fun android13Implementation(wifiManager: WifiManager): ApiCall<Boolean> {
    return coroutineScope {
        try {
            ApiCall.Success(wifiManager.isWifiPasspointEnabled)
        }catch (e: Exception) {
            ApiCall.Error(UiText.DynamicString(e.message!!))
        }
    }
}*/
