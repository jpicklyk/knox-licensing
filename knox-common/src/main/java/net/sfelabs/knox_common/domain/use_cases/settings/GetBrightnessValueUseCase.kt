package net.sfelabs.knox_common.domain.use_cases.settings

import android.content.Context
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.UiText
import javax.inject.Inject

class GetBrightnessValueUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(): ApiResult<Int> {
        return coroutineScope {
            try {
                ApiResult.Success(Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS))
            } catch (e: Settings.SettingNotFoundException) {
                ApiResult.Error(UiText.DynamicString(e.message!!))
            }
        }
    }
}