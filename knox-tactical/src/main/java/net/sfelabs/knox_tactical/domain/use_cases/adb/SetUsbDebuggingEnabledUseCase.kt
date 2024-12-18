package net.sfelabs.knox_tactical.domain.use_cases.adb

import com.samsung.android.knox.EnterpriseDeviceManager
import net.sfelabs.core.knox.android.KnoxContextAwareUseCase
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError

class SetUsbDebuggingUseCase : KnoxContextAwareUseCase<Boolean, Unit>() {
    val restrictionPolicy = EnterpriseDeviceManager.getInstance(knoxContext).restrictionPolicy

    override suspend fun execute(params: Boolean): ApiResult<Unit> {
        return when(restrictionPolicy.setUsbDebuggingEnabled(params)) {
            true -> ApiResult.Success(Unit)
            false -> ApiResult.Error(
                DefaultApiError.UnexpectedError(
                    "Unexpected setUsbDebuggingEnabled API error"
                )
            )
        }
    }
}