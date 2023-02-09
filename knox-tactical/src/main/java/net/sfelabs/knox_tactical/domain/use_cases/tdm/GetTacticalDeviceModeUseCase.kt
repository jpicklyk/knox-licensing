package net.sfelabs.knox_tactical.domain.use_cases.tdm

import com.samsung.android.knox.restriction.RestrictionPolicy
import kotlinx.coroutines.coroutineScope
import net.sfelabs.common.core.ApiCall
import net.sfelabs.common.core.ui.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class GetTacticalDeviceModeUseCase @Inject constructor(
    @TacticalSdk private val restrictionPolicy: RestrictionPolicy
) {
    suspend operator fun invoke(): ApiCall<Boolean> {
        return coroutineScope {
            try {
                ApiCall.Success(
                    data = restrictionPolicy.isTacticalDeviceModeEnabled
                )
            } catch (e: SecurityException) {
                ApiCall.Error(
                    UiText.DynamicString(
                        e.message ?: "Calling application does not have the required permission"
                    )
                )
            }
        }


    }
}