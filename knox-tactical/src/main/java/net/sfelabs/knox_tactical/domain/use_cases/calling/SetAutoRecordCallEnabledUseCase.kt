package net.sfelabs.knox_tactical.domain.use_cases.calling

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.core.ui.UnitApiCall
import net.sfelabs.core.ui.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class SetAutoRecordCallEnabledUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
){

    suspend operator fun invoke(enable: Boolean): net.sfelabs.core.ui.UnitApiCall {
        return coroutineScope {
            try {
                val result = systemManager.setAutomaticRecordCallEnabledState(enable)
                if(result == CustomDeviceManager.SUCCESS) {
                    net.sfelabs.core.ui.ApiCall.Success(Unit)
                } else {
                    net.sfelabs.core.ui.ApiCall.Error(net.sfelabs.core.ui.UiText.DynamicString("The API setAutomaticRecordCallEnabledState($enable) failed."))
                }
            } catch (e: SecurityException) {
                net.sfelabs.core.ui.ApiCall.Error(
                    net.sfelabs.core.ui.UiText.DynamicString(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_CUSTOM_SYSTEM\" permission"
                    ))
            } catch (nsm: NoSuchMethodError) {
                net.sfelabs.core.ui.ApiCall.NotSupported
            }
        }
    }
}