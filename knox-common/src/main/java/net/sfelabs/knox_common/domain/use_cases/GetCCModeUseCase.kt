package net.sfelabs.knox_common.domain.use_cases

import com.samsung.android.knox.EnterpriseKnoxManager
import net.sfelabs.core.knox.android.KnoxContextAwareUseCase
import net.sfelabs.core.knox.api.domain.ApiResult

class GetCCModeUseCase: KnoxContextAwareUseCase<Unit, Int>() {
    val restrictionPolicy =
        EnterpriseKnoxManager.getInstance(knoxContext).advancedRestrictionPolicy

    override suspend fun execute(params: Unit): ApiResult<Int> {
        return ApiResult.Success(restrictionPolicy.ccModeState)
    }
}