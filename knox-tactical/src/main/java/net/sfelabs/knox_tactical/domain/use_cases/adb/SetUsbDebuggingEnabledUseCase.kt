package net.sfelabs.knox_tactical.domain.use_cases.adb

import com.samsung.android.knox.EnterpriseDeviceManager
import net.sfelabs.core.knox.android.WithAndroidApplicationContext
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.CoroutineApiUseCase
import net.sfelabs.core.knox.api.domain.DefaultApiError

class SetUsbDebuggingUseCase : WithAndroidApplicationContext, CoroutineApiUseCase<Boolean, Unit>() {
    val restrictionPolicy = EnterpriseDeviceManager.getInstance(applicationContext).restrictionPolicy

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