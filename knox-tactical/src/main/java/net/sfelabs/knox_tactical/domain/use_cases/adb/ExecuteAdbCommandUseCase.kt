package net.sfelabs.knox_tactical.domain.use_cases.adb

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.CoroutineApiUseCase
import net.sfelabs.core.knox.api.domain.DefaultApiError
import net.sfelabs.knox_tactical.domain.model.AdbHeader
import net.sfelabs.knox_tactical.domain.use_cases.adb.ExecuteAdbCommandUseCase.AdbCommand

class ExecuteAdbCommandUseCase: CoroutineApiUseCase<AdbCommand, Unit>() {
    val systemManager = CustomDeviceManager.getInstance().systemManager

    suspend operator fun invoke(header: AdbHeader, command: String): ApiResult<Unit> {
        return invoke(AdbCommand(header, command))
    }

    override suspend fun execute(params: AdbCommand): ApiResult<Unit> {
        return when(val result = systemManager.executeAdbCommand(params.header.value, params.command)) {
            CustomDeviceManager.SUCCESS -> {
                ApiResult.Success(Unit)
            }
            else -> {
                ApiResult.Error(
                    DefaultApiError.UnexpectedError(
                        "ExecuteAdbCommand error: $result"
                    )
                )
            }
        }
    }

    data class AdbCommand(val header: AdbHeader, val command: String)
}