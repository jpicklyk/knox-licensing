package net.sfelabs.knox_common.domain.use_cases.license

import com.samsung.android.knox.license.ActivationInfo
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.UiText
import javax.inject.Inject

class GetLicenseActivationInfoUseCase @Inject constructor(
    private val licenseManager: KnoxEnterpriseLicenseManager
) {

    suspend operator fun invoke(): ApiResult<ActivationInfo> {
        return coroutineScope {
            val result = licenseManager.licenseActivationInfo
            if(result != null) {
                ApiResult.Success(result)
            } else {
                ApiResult.Error(UiText.DynamicString("No information returned for activation info"))
            }

        }
    }
}