package net.sfelabs.knox_tactical.domain.use_cases.ethernet

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.model.DefaultApiError
import net.sfelabs.knox_tactical.KnoxTacticalExtensions.configureDhcpEthernetInterface
import net.sfelabs.knox_tactical.KnoxTacticalExtensions.configureStaticEthernetInterface

class ConfigureEthernetInterfaceAltUseCase
    : SuspendingUseCase<ConfigureEthernetInterfaceAltUseCase.Params, Unit>() {
    class Params(
        val interfaceName: String,
        val useDhcp: Boolean,
        val ipAddress: String,
        val netmask: String,
        val dnsAddressList: List<String> = emptyList(),
        val defaultRouter: String? = null
    )

    private val systemManager = CustomDeviceManager.getInstance().systemManager

    suspend operator fun invoke(
        interfaceName: String,
        useDhcp: Boolean,
        ipAddress: String,
        netmask: String,
        dnsAddressList: List<String> = emptyList(),
        defaultRouter: String? = null
    ):UnitApiCall {
        return invoke(
            Params(
                interfaceName,
                useDhcp,
                ipAddress,
                netmask,
                dnsAddressList,
                defaultRouter
            )
        )
    }

    override suspend fun execute(params: Params): ApiResult<Unit> {
        val isSuccessful = when (params.useDhcp) {
            true -> systemManager.configureDhcpEthernetInterface(params.interfaceName)
            false -> systemManager.configureStaticEthernetInterface(
                params.interfaceName,
                params.ipAddress,
                params.dnsAddressList,
                params.defaultRouter,
                params.netmask
            )
        }
        return if (isSuccessful) {
            ApiResult.Success(Unit)
        } else {
            ApiResult.Error(DefaultApiError.UnexpectedError(
                "An unknown error occurred while configuring DHCP interface $params.interfaceName")
            )
        }
    }
}