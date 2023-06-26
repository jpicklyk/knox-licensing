package net.sfelabs.knox_tactical.domain.use_cases.adb

import android.util.Log
import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.core.ui.UiText
import net.sfelabs.core.ui.UnitApiCall
import net.sfelabs.knox_tactical.di.TacticalSdk
import net.sfelabs.knox_tactical.domain.model.AdbHeader
import javax.inject.Inject

class ExecuteAdbCommandUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {

    suspend operator fun invoke(header: AdbHeader, command: String): UnitApiCall {
        return coroutineScope {
            try {
                val result = systemManager.executeAdbCommand(header.value, command)
                if (result != CustomDeviceManager.SUCCESS) {
                    ApiCall.Error(
                        UiText.DynamicString(
                            "ExecuteAdbCommand error: $result"
                        ))
                } else {
                    ApiCall.Success(Unit)
                }

            } catch (se: SecurityException) {
                Log.e(null, se.message!!)
                ApiCall.Error(
                    UiText.DynamicString(
                    "The use of this API requires the caller to have the " +
                            "\"com.samsung.android.knox.permission.KNOX_CUSTOM_SETTING\" permission"
                ))
            }
        }
    }
}