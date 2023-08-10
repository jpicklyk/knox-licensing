package net.sfelabs.knox_common.domain.use_cases.license

import com.samsung.android.knox.license.ActivationInfo
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.core.ui.UiText
import javax.inject.Inject

class GetLicenseActivationInfoUseCase @Inject constructor(
    private val licenseManager: KnoxEnterpriseLicenseManager
) {

    suspend operator fun invoke(): ApiCall<ActivationInfo> {
        return coroutineScope {
            val result = licenseManager.licenseActivationInfo
            if(result != null) {
                ApiCall.Success(result)
            } else {
                ApiCall.Error(UiText.DynamicString("No information returned for activation info"))
            }

        }
    }
}