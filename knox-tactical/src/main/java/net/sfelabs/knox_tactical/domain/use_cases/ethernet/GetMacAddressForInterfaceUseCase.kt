package net.sfelabs.knox_tactical.domain.use_cases.ethernet

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.model.DefaultApiError
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase

class GetMacAddressForInterfaceUseCase: SuspendingUseCase<GetMacAddressForInterfaceUseCase.Params, String>() {
    class Params(
        val interfaceName: String
    )
    private val systemManager = CustomDeviceManager.getInstance().systemManager

    suspend operator fun invoke(interfaceName: String): ApiResult<String> {
        return invoke(Params(interfaceName))
    }

    override suspend fun execute(params: Params): ApiResult<String> {
        val result = systemManager.getMacAddressForEthernetInterface(params.interfaceName)
        return if (result == null) {
            ApiResult.Error(
                DefaultApiError.UnexpectedError(
                    "Ethernet interface doesn't exist"
                )
            )
        } else {
            ApiResult.Success(result)
        }
    }
}