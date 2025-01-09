package net.sfelabs.knox_common.domain.use_cases

import com.samsung.android.knox.EnterpriseDeviceManager
import net.sfelabs.core.knox.android.KnoxContextAwareUseCase
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError

class AllowUsbHostStorageUseCase: KnoxContextAwareUseCase<AllowUsbHostStorageUseCase.Params, Unit>() {
    class Params(val allow: Boolean)

    private val restrictionPolicy =
        EnterpriseDeviceManager.getInstance(knoxContext)
        .restrictionPolicy

    suspend fun invoke(allow: Boolean): ApiResult<Unit> {
        return invoke(Params(allow))
    }

    override suspend fun execute(params: Params): ApiResult<Unit> {
        val result = restrictionPolicy.allowUsbHostStorage(params.allow)
        return if (result)
            ApiResult.Success(Unit)
        else
            ApiResult.Error(
                DefaultApiError.UnexpectedError(
                    "The API allowUsbHostStorage($params.allow) failed"
                )
            )
    }
}