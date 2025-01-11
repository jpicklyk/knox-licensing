package net.sfelabs.knox_tactical.domain.use_cases.ethernet

import com.samsung.android.knox.custom.SettingsManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.knox.api.domain.model.ApiResult
import net.sfelabs.core.knox.api.domain.model.DefaultApiError
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class DeleteIpAddressFromEthernetInterfaceUseCase @Inject constructor(
    @TacticalSdk private val settingsManager: SettingsManager
) {

    suspend operator fun invoke(interfaceName: String, ipAddresses: String): UnitApiCall {
        return coroutineScope {
            try {
                ApiResult.Success(
                    settingsManager.deleteIpAddressToEthernetInterface(
                        interfaceName, ipAddresses
                    )
                )
            } catch(e: IllegalArgumentException) {
                ApiResult.Error(
                    DefaultApiError.UnexpectedError(
                        e.message ?: "An illegal argument was passed"
                    )
                )
            } catch (e: NoSuchMethodError) {
                ApiResult.NotSupported
            } catch (e: SecurityException) {
                ApiResult.Error(
                    DefaultApiError.UnexpectedError(
                        e.message ?: (
                                "The use of this API requires the caller to have permission " +
                                        "'com.samsung.android.knox.permission.KNOX_CUSTOM_SETTING'."
                                )
                    )
                )
            } catch (e: Exception) {
                ApiResult.Error(
                    DefaultApiError.UnexpectedError(
                        e.message ?: "An unknown error occurred"
                    )
                )
            }
        }
    }
}