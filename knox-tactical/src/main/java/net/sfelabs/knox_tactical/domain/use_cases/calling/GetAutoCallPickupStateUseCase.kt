package net.sfelabs.knox_tactical.domain.use_cases.calling

import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.core.domain.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import net.sfelabs.knox_tactical.domain.model.AutoCallPickupState
import javax.inject.Inject

/**
 * This Knox API isn't TE specific but the flag ENABLED_ALWAYS_ACCEPT is.
 */
class GetAutoCallPickupStateUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
){
    suspend operator fun invoke(): ApiCall<AutoCallPickupState> {
        return coroutineScope {
            try {
                ApiCall.Success(AutoCallPickupState.invoke(systemManager.autoCallPickupState))
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