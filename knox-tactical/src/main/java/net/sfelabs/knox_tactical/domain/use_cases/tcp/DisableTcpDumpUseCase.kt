package net.sfelabs.knox_tactical.domain.use_cases.tcp

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.SuspendingUseCase
import net.sfelabs.core.knox.api.domain.DefaultApiError

class DisableTcpDumpUseCase: SuspendingUseCase<Unit, Unit>() {
    private val systemManager = CustomDeviceManager.getInstance().systemManager

    override suspend fun execute(params: Unit): ApiResult<Unit> {
        val result = systemManager.disableTcpDump()
        return if (result != CustomDeviceManager.SUCCESS) {
            ApiResult.Error(
                DefaultApiError.UnexpectedError(
                    "DisableTcpDump error: $result"
                )
            )
        } else {
            ApiResult.Success(Unit)
        }
    }
}