package net.sfelabs.knox_tactical.domain.use_cases.usb

import com.samsung.android.knox.EnterpriseDeviceManager
import net.sfelabs.core.knox.android.KnoxContextAwareUseCase
import net.sfelabs.core.knox.api.domain.ApiResult

class GetPackagesFromUsbHostWhiteListUseCase: KnoxContextAwareUseCase<Unit,List<String>>() {
    private val appPolicy = EnterpriseDeviceManager.getInstance(knoxContext).applicationPolicy

    override suspend fun execute(params: Unit): ApiResult<List<String>> {
        return ApiResult.Success(appPolicy.packagesFromUsbHostWhiteList)
    }
}