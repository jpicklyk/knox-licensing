package net.sfelabs.knox_ngd2.domain.use_cases

import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.core.domain.UiText
import net.sfelabs.knox_ngd2.di.Ngd2Sdk
import javax.inject.Inject

class IsPogoKeyboardDisabledUseCase @Inject constructor(
    @Ngd2Sdk private val systemManager: SystemManager
) {
    suspend operator fun invoke(): ApiCall<Boolean> {
        return coroutineScope {
            try {
                ApiCall.Success(systemManager.isPOGOKeyboardConnectionDisabled)
            } catch (e: Exception) {
                ApiCall.Error(
                    UiText.DynamicString(
                        e.message!!
                    ))
            }
        }
    }
}