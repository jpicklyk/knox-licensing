package net.sfelabs.knox_common.domain.use_cases.settings

import android.content.Context
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError
import javax.inject.Inject

/**
 *
 */
class GetBrightnessModeUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(): ApiResult<Int> {
        return coroutineScope {
            try {
                ApiResult.Success(Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE))
            } catch (e: Settings.SettingNotFoundException) {
                ApiResult.Error(DefaultApiError.UnexpectedError(e.message!!))
            }
        }
    }
}