package net.sfelabs.knox_tactical.domain.use_cases.usb

import com.samsung.android.knox.EnterpriseDeviceManager
import net.sfelabs.core.knox.android.WithAndroidApplicationContext
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.CoroutineApiUseCase

class GetPackagesFromUsbHostWhiteListUseCase: WithAndroidApplicationContext, CoroutineApiUseCase<Unit,List<String>>() {
    private val appPolicy = EnterpriseDeviceManager.getInstance(applicationContext).applicationPolicy

    override suspend fun execute(params: Unit): ApiResult<List<String>> {
        return ApiResult.Success(appPolicy.packagesFromUsbHostWhiteList)
    }
}