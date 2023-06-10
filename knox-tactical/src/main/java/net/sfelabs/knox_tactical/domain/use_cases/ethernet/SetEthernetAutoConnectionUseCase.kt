package net.sfelabs.knox_tactical.domain.use_cases.ethernet

import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SettingsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import net.sfelabs.common.core.ApiCall
import net.sfelabs.common.core.ui.UiText
import net.sfelabs.common.core.UnitApiCall
import net.sfelabs.knox_tactical.di.TacticalSdk
import net.sfelabs.knox_tactical.domain.model.AutoConnectionState
import javax.inject.Inject

class SetEthernetAutoConnectionUseCase @Inject constructor(
    @TacticalSdk private val settingsManager: SettingsManager,
    private val connectivityManager: ConnectivityManager
) {

    operator fun invoke(
        autoConnectionState: AutoConnectionState,
        callbackList: List<NetworkCallback>
    ): Flow<UnitApiCall> = flow {
        if(autoConnectionState == AutoConnectionState.ON) {
            val result = settingsManager.setEthernetAutoConnectionState(autoConnectionState.state)
            if(result == CustomDeviceManager.SUCCESS)
                emit(ApiCall.Success(Unit))
            else emit(ApiCall.Error(
                uiText = UiText.DynamicString("Device does not support this feature"))
            )

        } else {
            callbackList.forEach { callback ->
                    try {
                        connectivityManager.unregisterNetworkCallback(callback)
                    } catch (_: IllegalArgumentException) {
                    }

            }
            val result = settingsManager.setEthernetAutoConnectionState(autoConnectionState.state)
            if(result == CustomDeviceManager.SUCCESS)
                emit(ApiCall.Success(Unit))
            else emit(ApiCall.Error(
                uiText = UiText.DynamicString("Device does not support this feature"))
            )
        }

    }.flowOn(Dispatchers.IO)
}