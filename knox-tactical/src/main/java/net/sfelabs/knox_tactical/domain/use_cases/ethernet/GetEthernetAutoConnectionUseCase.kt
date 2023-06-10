package net.sfelabs.knox_tactical.domain.use_cases.ethernet

import com.samsung.android.knox.custom.SettingsManager
import net.sfelabs.common.core.ApiCall
import net.sfelabs.common.core.ui.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import net.sfelabs.knox_tactical.domain.model.AutoConnectionState
import javax.inject.Inject

class GetEthernetAutoConnectionUseCase @Inject constructor(
    @TacticalSdk private val settingsManager: SettingsManager
) {

    operator fun invoke(): ApiCall<AutoConnectionState> {
        return try {
            ApiCall.Success(AutoConnectionState(settingsManager.ethernetAutoConnectionState))
        } catch (e: Exception) {
            ApiCall.Error(
                UiText.DynamicString(
                    e.message!!
                ))
        }
    }
}