package net.sfelabs.knox_tactical.domain.use_cases.hotspot

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SettingsManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.coroutineScope
import net.sfelabs.common.core.ApiCall
import net.sfelabs.common.core.ui.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import java.lang.Exception
import javax.inject.Inject

class GetHotspot20StateUseCase @Inject constructor(
        @TacticalSdk private val settingsManager: SettingsManager,
        private val wifiManager: WifiManager
    ){
        suspend operator fun invoke(): ApiCall<Boolean> {
            return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                android13Implementation(wifiManager)
            } else {
                android11Implementation(settingsManager)
            }
        }
    }

private suspend fun android11Implementation(settingsManager: SettingsManager): ApiCall<Boolean> {
    return coroutineScope {
        try {
            when(val result = settingsManager.hotspot20State) {
                CustomDeviceManager.ON -> ApiCall.Success(true)
                CustomDeviceManager.OFF -> ApiCall.Success(false)
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

@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private suspend fun android13Implementation(wifiManager: WifiManager): ApiCall<Boolean> {
    return coroutineScope {
        try {
            ApiCall.Success(wifiManager.isWifiPasspointEnabled)
        }catch (e: Exception) {
            ApiCall.Error(UiText.DynamicString(e.message!!))
        }
    }
}