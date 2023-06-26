package net.sfelabs.knox_ngd2.domain.use_cases

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.core.ui.UiText
import net.sfelabs.knox_ngd2.di.Ngd2Sdk
import javax.inject.Inject

class DisablePogoKeyboardUseCase @Inject constructor(
    @Ngd2Sdk private val systemManager: SystemManager
) {
    suspend operator fun invoke(disable: Boolean): net.sfelabs.core.ui.ApiCall<Int> {
        return coroutineScope {
            try {
                val result = systemManager.disablePOGOKeyboardConnection(disable)
                if(result == CustomDeviceManager.SUCCESS) {
                    net.sfelabs.core.ui.ApiCall.Success(result)
                } else {
                    net.sfelabs.core.ui.ApiCall.Error(
                        net.sfelabs.core.ui.UiText
                        .DynamicString(
                            "An error occurred while disabling POGO connection: $result"
                        )
                    )
                }
            } catch (e: Exception) {
                net.sfelabs.core.ui.ApiCall.Error(
                    net.sfelabs.core.ui.UiText.DynamicString(
                        e.message!!
                    ))
            }
        }
    }
}