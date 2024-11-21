package net.sfelabs.knox_common.license.domain.usecase

import com.samsung.android.knox.license.ActivationInfo
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError
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
                ApiResult.Error(DefaultApiError.UnexpectedError("No information returned for activation info"))
            }

        }
    }
}