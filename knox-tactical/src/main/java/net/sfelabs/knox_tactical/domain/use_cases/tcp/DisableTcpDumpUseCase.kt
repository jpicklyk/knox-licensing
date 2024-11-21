package net.sfelabs.knox_tactical.domain.use_cases.tcp

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class DisableTcpDumpUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
){

    suspend operator fun invoke(): UnitApiCall {
        return coroutineScope {
            try {
                val result = systemManager.disableTcpDump()
                if (result != CustomDeviceManager.SUCCESS) {
                    ApiResult.Error(
                        DefaultApiError.UnexpectedError(
                            "DisableTcpDump error: $result"
                        )
                    )
                } else {
                    ApiResult.Success(Unit)
                }
            } catch (se: SecurityException) {
                ApiResult.Error(
                    DefaultApiError.UnexpectedError(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_CUSTOM_SETTING\" permission"
                    )
                )
            }
        }
    }
}