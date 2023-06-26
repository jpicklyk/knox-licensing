package net.sfelabs.knox_tactical.domain.use_cases.tdm

import com.samsung.android.knox.restriction.RestrictionPolicy
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.core.ui.UiText
import net.sfelabs.core.ui.UnitApiCall
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class SetTacticalDeviceModeUseCase @Inject constructor(
    @TacticalSdk private val restrictionPolicy: RestrictionPolicy
) {

    suspend operator fun invoke(enable: Boolean): net.sfelabs.core.ui.UnitApiCall {
        return coroutineScope {
            try {

                val success = restrictionPolicy.enableTacticalDeviceMode(enable)
                if(success) net.sfelabs.core.ui.ApiCall.Success(Unit)
                else net.sfelabs.core.ui.ApiCall.Error(uiText =
                net.sfelabs.core.ui.UiText.DynamicString("An unknown error occurred")
                )
            }catch (e: SecurityException) {
                net.sfelabs.core.ui.ApiCall.Error(uiText =
                net.sfelabs.core.ui.UiText.DynamicString(
                    e.message?:"Calling application does not have the required permission"
                ))
            } catch (nsm: NoSuchMethodError) {
                net.sfelabs.core.ui.ApiCall.NotSupported
            }
        }


    }

}