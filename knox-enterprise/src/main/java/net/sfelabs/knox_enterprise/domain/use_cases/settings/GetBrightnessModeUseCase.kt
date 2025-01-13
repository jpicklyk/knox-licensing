package net.sfelabs.knox_enterprise.domain.use_cases.settings

import android.provider.Settings
import net.sfelabs.core.knox.android.WithAndroidApplicationContext
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase

/**
 *
 */
class GetBrightnessModeUseCase: WithAndroidApplicationContext, SuspendingUseCase<Unit, Int>() {

    suspend operator fun invoke(): ApiResult<Int> {
        return invoke(Unit)
    }

    override suspend fun execute(params: Unit): ApiResult<Int> {
        return ApiResult.Success(
            Settings.System.getInt(
                applicationContext.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE
            )
        )
    }
}