package net.sfelabs.knox_tactical.domain.use_cases.ethernet

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase

class AddIpAddressToEthernetInterfaceUseCase: SuspendingUseCase<AddIpAddressToEthernetInterfaceUseCase.Params, Unit>() {
    class Params(
        val interfaceName: String,
        val ipAddresses: String
    )
    private val settingsManager = CustomDeviceManager.getInstance().settingsManager

    suspend operator fun invoke(interfaceName: String, ipAddresses: String): UnitApiCall {
        return execute(Params(interfaceName, ipAddresses))
    }

    override suspend fun execute(params: Params): ApiResult<Unit> {
        return ApiResult.Success(
            settingsManager.addIpAddressToEthernetInterface(
                params.interfaceName, params.ipAddresses
            )
        )
    }
}