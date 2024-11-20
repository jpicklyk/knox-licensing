package net.sfelabs.knox_tactical.domain.api.tdm

import com.samsung.android.knox.restriction.RestrictionPolicy
import kotlinx.coroutines.CoroutineDispatcher
import net.sfelabs.core.knoxfeature.annotation.GeneratedFeatureUseCase
import net.sfelabs.core.knoxfeature.domain.usecase.base.CoroutineFeatureUseCase
import net.sfelabs.knox_tactical.di.TacticalSdk
import net.sfelabs.knox_tactical.domain.api.TacticalRestrictions

@GeneratedFeatureUseCase(
    feature = TacticalRestrictions.TacticalDeviceMode::class,
    defaultBlocking = true
)
abstract class GetTacticalDeviceModeUseCase (
    @TacticalSdk private val restrictionPolicy: RestrictionPolicy,
    dispatcher: CoroutineDispatcher? = null
) : CoroutineFeatureUseCase<Boolean, Unit, Boolean>(dispatcher)