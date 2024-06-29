package net.sfelabs.knox_tactical.domain.use_cases.tdm

import com.samsung.android.knox.restriction.RestrictionPolicy
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.UiText
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class SetTacticalDeviceModeUseCase @Inject constructor(
    @TacticalSdk private val restrictionPolicy: RestrictionPolicy
) {

    suspend operator fun invoke(enable: Boolean): UnitApiCall {
        return coroutineScope {
            try {

                val success = restrictionPolicy.enableTacticalDeviceMode(enable)
                if(success) ApiResult.Success(Unit)
                else ApiResult.Error(uiText =
                UiText.DynamicString("An unknown error occurred")
                )
            }catch (e: SecurityException) {
                ApiResult.Error(uiText =
                UiText.DynamicString(
                    e.message?:"Calling application does not have the required permission"
                ))
            } catch (nsm: NoSuchMethodError) {
                ApiResult.NotSupported
            }
        }


    }

}