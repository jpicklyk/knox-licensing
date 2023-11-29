package net.sfelabs.knox_common.domain.use_cases.settings

import android.content.Context
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.core.domain.UiText
import javax.inject.Inject

/**
 *
 */
class GetBrightnessModeUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(enable: Boolean, level: Int): ApiCall<Int> {
        return coroutineScope {
            try {
                ApiCall.Success(Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE))
            } catch (e: Settings.SettingNotFoundException) {
                ApiCall.Error(UiText.DynamicString(e.message!!))
            }
        }
    }
}