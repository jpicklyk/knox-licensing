package net.sfelabs.knox_common.license.domain.usecase

import com.samsung.android.knox.license.ActivationInfo
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager
import net.sfelabs.core.knox.android.KnoxContextAwareUseCase
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError

class GetLicenseActivationInfoUseCase: KnoxContextAwareUseCase<Unit, ActivationInfo>() {
    private val licenseManager = KnoxEnterpriseLicenseManager.getInstance(knoxContext)

    override suspend fun execute(params: Unit): ApiResult<ActivationInfo> {
        val result = licenseManager.licenseActivationInfo
        return if(result != null) {
            ApiResult.Success(result)
        } else {
            ApiResult.Error(
                DefaultApiError.UnexpectedError("No information returned for activation info"))
        }
    }
}