package net.sfelabs.knox_tactical.domain.use_cases.wifi

import com.samsung.android.knox.restriction.RestrictionPolicy
import kotlinx.coroutines.coroutineScope
import net.sfelabs.common.core.ApiCall
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class GetRandomizedMacAddressEnabledUseCase @Inject constructor(
    @TacticalSdk private val restrictionPolicy: RestrictionPolicy
) {
    suspend operator fun invoke(): ApiCall<Boolean> {
        return coroutineScope {
            try {
                ApiCall.Success(restrictionPolicy.isRandomisedMacAddressEnabled)
            } catch (ex: NoSuchMethodException) {
                ApiCall.NotSupported
            }
        }
    }
}