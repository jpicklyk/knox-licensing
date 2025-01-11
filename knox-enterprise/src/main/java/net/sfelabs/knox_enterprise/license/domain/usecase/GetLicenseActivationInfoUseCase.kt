package net.sfelabs.knox_enterprise.license.domain.usecase

import com.samsung.android.knox.license.ActivationInfo
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager
import net.sfelabs.core.knox.android.WithAndroidApplicationContext
import net.sfelabs.core.knox.api.domain.model.ApiResult
import net.sfelabs.core.knox.api.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.knox.api.domain.model.DefaultApiError

class GetLicenseActivationInfoUseCase: WithAndroidApplicationContext, SuspendingUseCase<Unit, ActivationInfo>() {
    private val licenseManager = KnoxEnterpriseLicenseManager.getInstance(applicationContext)

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