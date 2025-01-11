package net.sfelabs.knox_common.domain.use_cases.settings

import android.provider.Settings
import net.sfelabs.core.knox.android.WithAndroidApplicationContext
import net.sfelabs.core.knox.api.domain.model.ApiResult
import net.sfelabs.core.knox.api.domain.usecase.base.SuspendingUseCase

class GetBrightnessValueUseCase : WithAndroidApplicationContext, SuspendingUseCase<Unit, Int>() {

    override suspend fun execute(params: Unit): ApiResult<Int> {
        return ApiResult.Success(
            Settings.System.getInt(this@GetBrightnessValueUseCase.applicationContext.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
        )
    }
}