package net.sfelabs.knox_tactical.domain.use_cases.hotspot

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SettingsManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.core.domain.UiText
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class SetHotspot20StateUseCase @Inject constructor(
    @TacticalSdk private val settingsManager: SettingsManager
){

    suspend operator fun invoke(on: Boolean): UnitApiCall {
        return coroutineScope {
            try {
                val result =
                if(on) {
                    settingsManager.setHotspot20State(CustomDeviceManager.ON)
                } else {
                    settingsManager.setHotspot20State(CustomDeviceManager.OFF)
                }
                if(result == CustomDeviceManager.SUCCESS) {
                    ApiCall.Success(Unit)
                } else {
                    ApiCall.Error(UiText.DynamicString("The operation failed for an unknown reason"))
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