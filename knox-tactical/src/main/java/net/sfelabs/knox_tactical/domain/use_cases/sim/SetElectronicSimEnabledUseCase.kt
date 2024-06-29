package net.sfelabs.knox_tactical.domain.use_cases.sim

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SettingsManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.UiText
import net.sfelabs.core.domain.UnitApiCall
import javax.inject.Inject

class SetElectronicSimEnabledUseCase  @Inject constructor(
    private val settingsManager: SettingsManager
) {

    suspend operator fun invoke(enable: Boolean): UnitApiCall {
        return coroutineScope {
            try {
                val result = settingsManager.setEsimEnabled(enable)
                if(result == CustomDeviceManager.SUCCESS)
                    ApiResult.Success(Unit)
                else
                    ApiResult.Error(UiText.DynamicString("setEsimEnabled API call failed"))
            } catch (e: NoSuchMethodError) {
              ApiResult.NotSupported
            } catch (e: SecurityException) {
                println(e.message)
                ApiResult.Error(UiText.DynamicString(
                    "The use of this API requires the caller to have the " +
                        "\"com.samsung.android.knox.permission.KNOX_CUSTOM_SETTINGS\" permission"
                ))
            }
        }

    }
}