package net.sfelabs.knox_tactical.domain.use_cases.hotspot

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SettingsManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.core.domain.ApiResult
import net.sfelabs.core.domain.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class GetHotspot20StateUseCase @Inject constructor(
        @TacticalSdk private val settingsManager: SettingsManager
    ) {
        suspend operator fun invoke(): ApiCall<ApiResult<Int>> {
            return coroutineScope {
                try {
                    when(val result = settingsManager.hotspot20State) {
                        CustomDeviceManager.ON -> ApiCall.Success(ApiResult(true, result))
                        CustomDeviceManager.OFF -> ApiCall.Success(ApiResult(false, result))
                        else -> ApiCall.Error(UiText.DynamicString("Unexpected value returned: $result"))
                    }
                } catch (e: SecurityException) {
                    ApiCall.Error(
                        UiText.DynamicString(
                            "The use of this API requires the caller to have the " +
                                    "\"com.samsung.android.knox.permission.KNOX_CUSTOM_SETTING\" permission"
                        ))
                } catch (nsm: NoSuchMethodError) {
                    ApiCall.NotSupported
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
