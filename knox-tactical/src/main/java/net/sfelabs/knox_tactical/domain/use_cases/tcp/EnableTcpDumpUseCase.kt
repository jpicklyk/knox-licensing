package net.sfelabs.knox_tactical.domain.use_cases.tcp

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.domain.usecase.model.DefaultApiError

class EnableTcpDumpUseCase: SuspendingUseCase<EnableTcpDumpUseCase.Params, Unit>() {
    data class Params(val command: String)

    private val systemManager = CustomDeviceManager.getInstance().systemManager

    suspend operator fun invoke(command: String): UnitApiCall {
        return invoke(Params(command))
    }

    override suspend fun execute(params: Params): ApiResult<Unit> {
        val result = systemManager.enableTcpDump(params.command)
        return if (result != CustomDeviceManager.SUCCESS) {
            ApiResult.Error(
                DefaultApiError.UnexpectedError(
                    "EnableTcpDump error: $result"
                )
            )
        } else {
            ApiResult.Success(Unit)
        }
    }
}