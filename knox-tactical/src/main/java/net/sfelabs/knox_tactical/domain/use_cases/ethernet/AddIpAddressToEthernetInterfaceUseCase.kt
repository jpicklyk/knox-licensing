package net.sfelabs.knox_tactical.domain.use_cases.ethernet

import com.samsung.android.knox.custom.SettingsManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.core.domain.UiText
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class AddIpAddressToEthernetInterfaceUseCase @Inject constructor(
    @TacticalSdk private val settingsManager: SettingsManager
) {

    suspend operator fun invoke(interfaceName: String, ipAddresses: List<String>): UnitApiCall {
        return coroutineScope {
            try {
                ApiCall.Success(
                    settingsManager.addIpAddressToEthernetInterface(
                        interfaceName, ipAddresses
                    )
                )
            } catch(e: IllegalArgumentException) {
                ApiCall.Error(UiText.DynamicString(
                    e.message ?: "An illegal argument was passed"
                ))
            } catch (e: NoSuchMethodError) {
                ApiCall.NotSupported
            } catch (e: SecurityException) {
                ApiCall.Error(UiText.DynamicString(
                    e.message ?: (
                            "The use of this API requires the caller to have permission " +
                            "'com.samsung.android.knox.permission.KNOX_CUSTOM_SETTING'."
                            )
                ))
            }
        }
    }
}