package net.sfelabs.knox_tactical.domain.use_cases.backlight

import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.core.ui.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class GetBacklightStateUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {

    suspend operator fun invoke(): net.sfelabs.core.ui.ApiCall<Boolean> {
        return coroutineScope {
            try {
                net.sfelabs.core.ui.ApiCall.Success(systemManager.lcdBacklightState)
            } catch (ex: SecurityException) {
                net.sfelabs.core.ui.ApiCall.Error(
                    net.sfelabs.core.ui.UiText.DynamicString(
                    "The use of this API requires the caller to have the " +
                            "\"com.samsung.android.knox.permission.KNOX_CUSTOM_SETTING\" permission"
                ))
            }
        }
    }
}