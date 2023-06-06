package net.sfelabs.knox_tactical.domain.use_cases.wifi

import com.samsung.android.knox.restriction.RestrictionPolicy
import kotlinx.coroutines.coroutineScope
import net.sfelabs.common.core.ApiCall
import net.sfelabs.common.core.UnitApiCall
import net.sfelabs.common.core.ui.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class EnableRandomizedMacAddressUseCase @Inject constructor(
    @TacticalSdk private val restrictionPolicy: RestrictionPolicy
) {
    suspend operator fun invoke(enable: Boolean): UnitApiCall {
        return coroutineScope {
            try {
                val result = restrictionPolicy.enableRandomisedMacAddress(enable)
                if (result)
                    ApiCall.Success(Unit)
                else
                    ApiCall.Error(UiText.DynamicString("Setting Randomized Mac Address failed"))
            } catch(se: SecurityException) {
                ApiCall.Error(UiText.DynamicString(
                    "The use of this API requires the caller to have the " +
                            "\"com.samsung.android.knox.permission.KNOX_RESTRICTION_MGMT\" " +
                            "permission."
                ))
            }catch (ex: NoSuchMethodException) {
                ApiCall.NotSupported
            }
        }
    }
}