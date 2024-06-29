package net.sfelabs.knox_tactical.domain.use_cases.radio

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.CoroutineDispatcher
import net.sfelabs.core.di.IoDispatcher
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.api.feature.FeatureState
import net.sfelabs.core.domain.use_case.CoroutineApiUseCase
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class Get5gBandLockingUseCase @Inject constructor(
    @IoDispatcher defaultDispatcher: CoroutineDispatcher,
    @TacticalSdk private val systemManager: SystemManager
) : CoroutineApiUseCase<Unit, FeatureState<Int>>(defaultDispatcher) {

    override suspend fun execute(params: Unit?): ApiResult<FeatureState<Int>> {
        return when(val result = systemManager.get5GBandLocking()) {
            CustomDeviceManager.BANDLOCK_NONE -> ApiResult.Success(FeatureState(false, result))
            else -> ApiResult.Success(FeatureState(true, result))
        }
    }
}