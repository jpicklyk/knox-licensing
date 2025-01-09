package net.sfelabs.knox_common.domain.use_cases.settings

import android.provider.Settings
import net.sfelabs.core.knox.android.KnoxContextAwareUseCase
import net.sfelabs.core.knox.api.domain.ApiResult

class GetBrightnessValueUseCase : KnoxContextAwareUseCase<Unit, Int>() {

    override suspend fun execute(params: Unit): ApiResult<Int> {
        return ApiResult.Success(
            Settings.System.getInt(knoxContext.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
        )
    }
}