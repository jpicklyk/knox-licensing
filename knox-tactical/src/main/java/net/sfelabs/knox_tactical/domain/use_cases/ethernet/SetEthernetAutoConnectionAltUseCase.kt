package net.sfelabs.knox_tactical.domain.use_cases.ethernet

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SettingsManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.core.domain.UiText
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class SetEthernetAutoConnectionAltUseCase @Inject constructor(
    @TacticalSdk private val settingsManager: SettingsManager
) {
    suspend operator fun invoke(enable: Boolean): UnitApiCall {
        return coroutineScope {
            try {
                val result = settingsManager.setEthernetAutoConnectionState(
                    if(enable) 1 else 0
                )
                if(result == CustomDeviceManager.SUCCESS) {
                    ApiCall.Success(Unit)
                } else {
                    ApiCall.Error(UiText.DynamicString("Device does not support this method"))
                }
            } catch (e: SecurityException) {
                ApiCall.Error(UiText.DynamicString(e.message!!))
            } catch (e: NoSuchMethodError) {
                ApiCall.Error(UiText.DynamicString("Device does not support this method"))
            }
        }
    }

}