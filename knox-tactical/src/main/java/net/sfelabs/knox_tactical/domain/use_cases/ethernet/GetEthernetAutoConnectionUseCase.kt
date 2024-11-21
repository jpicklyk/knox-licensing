package net.sfelabs.knox_tactical.domain.use_cases.ethernet

import com.samsung.android.knox.custom.SettingsManager
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError
import net.sfelabs.knox_tactical.di.TacticalSdk
import net.sfelabs.knox_tactical.domain.model.AutoConnectionState
import javax.inject.Inject

class GetEthernetAutoConnectionUseCase @Inject constructor(
    @TacticalSdk private val settingsManager: SettingsManager
) {

    operator fun invoke(): ApiResult<AutoConnectionState> {
        return try {
            ApiResult.Success(AutoConnectionState(settingsManager.ethernetAutoConnectionState))
        } catch (e: Exception) {
            ApiResult.Error(
                DefaultApiError.UnexpectedError(
                    e.message!!
                )
            )
        }
    }
}