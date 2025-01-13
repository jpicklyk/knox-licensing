package net.sfelabs.knox_tactical.domain.use_cases.ethernet

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.model.DefaultApiError

class GetEthernetInterfaceNameForMacAddressUseCase
    : SuspendingUseCase<GetEthernetInterfaceNameForMacAddressUseCase.Params, String>() {
    class Params(
        val macAddress: String
    )
    private val systemManager = CustomDeviceManager.getInstance().systemManager

    suspend operator fun invoke(macAddress: String): ApiResult<String> {
        return invoke(Params(macAddress))
    }

    override suspend fun execute(params: Params): ApiResult<String> {
        val result = systemManager.getEthernetInterfaceNameForMacAddress(params.macAddress)
        return if(result == null) {
            ApiResult.Error(
                DefaultApiError.UnexpectedError(
                    "MAC Address doesn't exist"
                )
            )
        } else {
            ApiResult.Success(result)
        }
    }
}