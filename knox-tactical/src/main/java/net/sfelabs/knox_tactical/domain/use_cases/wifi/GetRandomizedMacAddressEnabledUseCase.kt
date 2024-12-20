package net.sfelabs.knox_tactical.domain.use_cases.wifi

import com.samsung.android.knox.EnterpriseDeviceManager
import net.sfelabs.core.knox.android.KnoxContextAwareUseCase
import net.sfelabs.core.knox.api.domain.ApiResult

class GetRandomizedMacAddressEnabledUseCase: KnoxContextAwareUseCase<Unit, Boolean>() {
    private val restrictionPolicy =
        EnterpriseDeviceManager.getInstance(knoxContext).restrictionPolicy

    override suspend fun execute(params: Unit): ApiResult<Boolean> {
        return ApiResult.Success(restrictionPolicy.isRandomisedMacAddressEnabled)
    }
}