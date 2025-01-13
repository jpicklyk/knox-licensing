package net.sfelabs.knox_tactical.domain.use_cases.ethernet

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.domain.usecase.model.ApiResult

class ListIpAddressesUseCase: SuspendingUseCase<ListIpAddressesUseCase.Params, List<String>>() {
    class Params(
        val interfaceName: String
    )
    private val settingsManager = CustomDeviceManager.getInstance().settingsManager

    suspend operator fun invoke(interfaceName: String): ApiResult<List<String>> {
        return invoke(Params(interfaceName))
    }

    override suspend fun execute(params: Params): ApiResult<List<String>> {
        return ApiResult.Success(
            settingsManager.listIpAddress(params.interfaceName)
        )
    }
}