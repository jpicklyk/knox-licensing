package net.sfelabs.knox_tactical.domain.use_cases.tcp

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.common.core.ApiCall
import net.sfelabs.common.core.ui.UiText
import net.sfelabs.common.core.UnitApiCall
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
                    ApiCall.Error(
                        UiText.DynamicString(
                            "DisableTcpDump error: $result"
                        ))
                } else {
                    ApiCall.Success(Unit)
                }
            } catch (se: SecurityException) {
                ApiCall.Error(
                    UiText.DynamicString(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_CUSTOM_SETTING\" permission"
                    ))
            }
        }
    }
}