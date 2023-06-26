package net.sfelabs.knox_tactical.domain.use_cases.ethernet

import com.samsung.android.knox.custom.SettingsManager
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.core.ui.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import net.sfelabs.knox_tactical.domain.model.AutoConnectionState
import javax.inject.Inject

class GetEthernetAutoConnectionUseCase @Inject constructor(
    @TacticalSdk private val settingsManager: SettingsManager
) {

    operator fun invoke(): net.sfelabs.core.ui.ApiCall<AutoConnectionState> {
        return try {
            net.sfelabs.core.ui.ApiCall.Success(AutoConnectionState(settingsManager.ethernetAutoConnectionState))
        } catch (e: Exception) {
            net.sfelabs.core.ui.ApiCall.Error(
                net.sfelabs.core.ui.UiText.DynamicString(
                    e.message!!
                ))
        }
    }
}