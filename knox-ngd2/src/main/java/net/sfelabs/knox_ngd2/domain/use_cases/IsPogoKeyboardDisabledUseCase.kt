package net.sfelabs.knox_ngd2.domain.use_cases

import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.core.ui.UiText
import net.sfelabs.knox_ngd2.di.Ngd2Sdk
import javax.inject.Inject

class IsPogoKeyboardDisabledUseCase @Inject constructor(
    @Ngd2Sdk private val systemManager: SystemManager
) {
    suspend operator fun invoke(): net.sfelabs.core.ui.ApiCall<Boolean> {
        return coroutineScope {
            try {
                net.sfelabs.core.ui.ApiCall.Success(systemManager.isPOGOKeyboardConnectionDisabled)
            } catch (e: Exception) {
                net.sfelabs.core.ui.ApiCall.Error(
                    net.sfelabs.core.ui.UiText.DynamicString(
                        e.message!!
                    ))
            }
        }
    }
}