package net.sfelabs.knox_ngd2.domain.use_cases

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.common.core.ApiCall
import net.sfelabs.common.core.ui.UiText
import net.sfelabs.knox_ngd2.di.Ngd2Sdk
import javax.inject.Inject

class DisablePogoKeyboardUseCase @Inject constructor(
    @Ngd2Sdk private val systemManager: SystemManager
) {
    suspend operator fun invoke(disable: Boolean): ApiCall<Int> {
        return coroutineScope {
            try {
                val result = systemManager.disablePOGOKeyboardConnection(disable)
                if(result == CustomDeviceManager.SUCCESS) {
                    ApiCall.Success(result)
                } else {
                    ApiCall.Error(UiText
                        .DynamicString(
                            "An error occurred while disabling POGO connection: $result"
                        )
                    )
                }
            } catch (e: Exception) {
                ApiCall.Error(
                    UiText.DynamicString(
                        e.message!!
                    ))
            }
        }
    }
}