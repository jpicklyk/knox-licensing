package net.sfelabs.knox_common.domain.use_cases

import com.samsung.android.knox.EnterpriseDeviceManager
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.knox.android.KnoxContextAwareUseCase
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError

class SetUsbExceptionListUseCase: KnoxContextAwareUseCase<SetUsbExceptionListUseCase.Params, Unit>() {
    class Params(val usbClassList: Int)

    private val restrictionPolicy =
        EnterpriseDeviceManager.getInstance(knoxContext).restrictionPolicy

    suspend operator fun invoke(usbClassList: Int): UnitApiCall {
        return invoke(Params(usbClassList))
    }

    override suspend fun execute(params: Params): ApiResult<Unit> {
        return if (restrictionPolicy.setUsbExceptionList(params.usbClassList))
            ApiResult.Success(Unit)
        else
            ApiResult.Error(
                DefaultApiError.UnexpectedError(
                    "The API setUsbExceptionList($params.usbClassList) failed"
                )
            )
    }
}