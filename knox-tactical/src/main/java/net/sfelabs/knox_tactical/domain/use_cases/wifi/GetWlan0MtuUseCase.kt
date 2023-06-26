package net.sfelabs.knox_tactical.domain.use_cases.wifi

import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.core.ui.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class GetWlan0MtuUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {

    suspend operator fun invoke(): net.sfelabs.core.ui.ApiCall<Int> {
        return coroutineScope {
            try {
                net.sfelabs.core.ui.ApiCall.Success(systemManager.knoxWlanZeroMtu)
            } catch (ex: SecurityException) {
                net.sfelabs.core.ui.ApiCall.Error(
                    net.sfelabs.core.ui.UiText.DynamicString(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_CUSTOM_SYSTEM\" permission"
                    ))
            }
        }
    }
}