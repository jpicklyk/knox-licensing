package net.sfelabs.knox_tactical.domain.use_cases.ramplus

import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.common.core.ApiCall
import net.sfelabs.common.core.ui.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class GetRamPlusDisabledStateUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
){

    suspend operator fun invoke(): ApiCall<Boolean> {
        return coroutineScope {
            try {
                val result = systemManager.ramPlusDisableState
                ApiCall.Success(result)
            } catch (e: SecurityException) {
                ApiCall.Error(
                    UiText.DynamicString(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_CUSTOM_SYSTEM\" permission"
                    ))
            } catch (nsm: NoSuchMethodError) {
                ApiCall.NotSupported
            }
        }
    }
}