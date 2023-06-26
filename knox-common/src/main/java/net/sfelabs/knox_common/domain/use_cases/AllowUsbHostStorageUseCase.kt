package net.sfelabs.knox_common.domain.use_cases

import com.samsung.android.knox.restriction.RestrictionPolicy
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.core.ui.UnitApiCall
import net.sfelabs.core.ui.UiText
import javax.inject.Inject

class AllowUsbHostStorageUseCase @Inject constructor(
    private val restrictionPolicy: RestrictionPolicy
) {
    suspend operator fun invoke(allow: Boolean): net.sfelabs.core.ui.UnitApiCall {
        return coroutineScope {
            try {
                val result = restrictionPolicy.allowUsbHostStorage(allow)
                if (result)
                    net.sfelabs.core.ui.ApiCall.Success(Unit)
                else
                    net.sfelabs.core.ui.ApiCall.Error(net.sfelabs.core.ui.UiText.DynamicString("The API allowUsbHostStorage($allow) failed"))
            } catch(se: SecurityException) {
                net.sfelabs.core.ui.ApiCall.Error(
                    net.sfelabs.core.ui.UiText.DynamicString(
                    "The use of this API requires the caller to have the " +
                            "\"com.samsung.android.knox.permission.KNOX_RESTRICTION_MGMT\" " +
                            "permission which has a protection level of signature."
                ))
            }catch (ex: NoSuchMethodException) {
                net.sfelabs.core.ui.ApiCall.NotSupported
            }
        }
    }
}