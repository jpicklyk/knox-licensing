package net.sfelabs.knox_tactical.domain.use_cases.wifi

import com.samsung.android.knox.restriction.RestrictionPolicy
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.core.domain.KnoxApiEnabled
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class GetRandomizedMacAddressEnabledUseCase @Inject constructor(
    @TacticalSdk private val restrictionPolicy: RestrictionPolicy
): KnoxApiEnabled {
    suspend operator fun invoke(): ApiCall<Boolean> {
        return coroutineScope {
            try {
                ApiCall.Success(restrictionPolicy.isRandomisedMacAddressEnabled)
            } catch (ex: NoSuchMethodException) {
                ApiCall.NotSupported
            }
        }
    }

    override suspend fun isApiEnabled(): ApiCall<Boolean> {
        return invoke()
    }

}