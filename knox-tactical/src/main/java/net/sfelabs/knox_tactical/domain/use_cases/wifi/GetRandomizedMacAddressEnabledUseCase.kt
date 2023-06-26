package net.sfelabs.knox_tactical.domain.use_cases.wifi

import com.samsung.android.knox.restriction.RestrictionPolicy
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class GetRandomizedMacAddressEnabledUseCase @Inject constructor(
    @TacticalSdk private val restrictionPolicy: RestrictionPolicy
) {
    suspend operator fun invoke(): net.sfelabs.core.ui.ApiCall<Boolean> {
        return coroutineScope {
            try {
                net.sfelabs.core.ui.ApiCall.Success(restrictionPolicy.isRandomisedMacAddressEnabled)
            } catch (ex: NoSuchMethodException) {
                net.sfelabs.core.ui.ApiCall.NotSupported
            }
        }
    }
}