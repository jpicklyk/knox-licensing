package net.sfelabs.knox_tactical.domain.use_cases.ethernet

import com.samsung.android.knox.custom.SettingsManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class ListIpAddressesUseCase @Inject constructor(
    @TacticalSdk private val settingsManager: SettingsManager
) {

    suspend operator fun invoke(interfaceName: String): ApiResult<List<String>> {
        return coroutineScope {
            try {
                ApiResult.Success(
                    settingsManager.listIpAddress(interfaceName)
                )
            } catch(e: IllegalArgumentException) {
                ApiResult.Error(
                    UiText.DynamicString(
                        e.message ?: "An illegal argument was passed"
                    ))
            } catch (e: NoSuchMethodError) {
                ApiResult.NotSupported
            } catch (e: SecurityException) {
                ApiResult.Error(
                    UiText.DynamicString(
                        e.message ?: (
                                "The use of this API requires the caller to have permission " +
                                        "'com.samsung.android.knox.permission.KNOX_CUSTOM_SETTING'."
                                )
                    ))
            }
        }
    }
}