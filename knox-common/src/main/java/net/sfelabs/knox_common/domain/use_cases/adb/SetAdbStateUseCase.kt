package net.sfelabs.knox_common.domain.use_cases.adb

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SettingsManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError
import javax.inject.Inject

/**
 * Use case to turn on or off the Android Debug Bridge.
 * Note: There is no getAdbState equivalent API so it will be necessary to track the state within
 * the app and stave it to a datastore
 */
class SetAdbStateUseCase @Inject constructor(
    private val settingsManager: SettingsManager
) {

    suspend operator fun invoke(enable: Boolean): ApiResult<Boolean> {
        return coroutineScope {
            try {
                when (val result = settingsManager.setAdbState(enable)) {
                    CustomDeviceManager.SUCCESS -> {
                        ApiResult.Success(data = enable)
                    }

                    CustomDeviceManager.ERROR_POLICY_RESTRICTED -> {
                        ApiResult.Error(DefaultApiError.UnexpectedError("ERROR_POLICY_RESTRICTED: USB debugging has been disabled via API"))
                    }

                    else -> {
                        ApiResult.Error(DefaultApiError.UnexpectedError("An error occurred calling setAdbState: $result"))
                    }
                }
            } catch (se: SecurityException) {
                ApiResult.Error(
                    DefaultApiError.UnexpectedError(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_CUSTOM_SETTING\" permission"
                    )
                )
            }
        }
    }
}