package net.sfelabs.knox_tactical.domain.use_cases.sim

import android.content.Context
import com.samsung.android.knox.EnterpriseDeviceManager
import com.samsung.android.knox.custom.CustomDeviceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.core.domain.UiText
import net.sfelabs.core.domain.UnitApiCall
import javax.inject.Inject

class EnableSimPowerStateUseCase  @Inject constructor(
    @ApplicationContext private val context: Context
) {

    suspend operator fun invoke(enable: Boolean): UnitApiCall {
        return coroutineScope {
            try {
                val phoneRestrictionPolicy = EnterpriseDeviceManager.getInstance(context)
                    .phoneRestrictionPolicy
                if(enable) {
                    ApiCall.Success(phoneRestrictionPolicy.setSimPowerState(CustomDeviceManager.ON))
                } else {
                    ApiCall.Success(phoneRestrictionPolicy.setSimPowerState(CustomDeviceManager.OFF))
                }

            } catch (e: NoSuchMethodError) {
              ApiCall.NotSupported
            } catch (e: SecurityException) {
                println(e.message)
                ApiCall.Error(UiText.DynamicString(
                    "The use of this API requires the caller to have the " +
                        "\"com.samsung.android.knox.permission.KNOX_PHONE_RESTRICTION\" permission"
                ))
            }
        }

    }
}