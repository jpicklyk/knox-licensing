package net.sfelabs.knox_tactical.domain.use_cases.wifi

import com.samsung.android.knox.restriction.RestrictionPolicy
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.core.ui.UnitApiCall
import net.sfelabs.core.ui.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class EnableRandomizedMacAddressUseCase @Inject constructor(
    @TacticalSdk private val restrictionPolicy: RestrictionPolicy
) {
    suspend operator fun invoke(enable: Boolean): net.sfelabs.core.ui.UnitApiCall {
        return coroutineScope {
            try {
                val result = restrictionPolicy.enableRandomisedMacAddress(enable)
                if (result)
                    net.sfelabs.core.ui.ApiCall.Success(Unit)
                else
                    net.sfelabs.core.ui.ApiCall.Error(net.sfelabs.core.ui.UiText.DynamicString("Setting Randomized Mac Address failed"))
            } catch(se: SecurityException) {
                net.sfelabs.core.ui.ApiCall.Error(
                    net.sfelabs.core.ui.UiText.DynamicString(
                    "The use of this API requires the caller to have the " +
                            "\"com.samsung.android.knox.permission.KNOX_RESTRICTION_MGMT\" " +
                            "permission."
                ))
            }catch (ex: NoSuchMethodException) {
                net.sfelabs.core.ui.ApiCall.NotSupported
            }
        }
    }
}