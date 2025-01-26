package net.sfelabs.knox_tactical.domain.policy

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.PolicyDefinition
import net.sfelabs.core.knox.feature.api.BooleanStatePolicy
import net.sfelabs.core.knox.feature.api.PolicyCategory
import net.sfelabs.knox_tactical.domain.use_cases.screen.GetExtraBrightnessUseCase
import net.sfelabs.knox_tactical.domain.use_cases.screen.SetExtraBrightnessUseCase

@PolicyDefinition(
    title = "Enable Extra Brightness",
    description = "Enables the screen extra brightness device settings",
    category = PolicyCategory.Toggle
)
class EnableExtraBrightnessPolicy: BooleanStatePolicy() {
    private val getUseCase = GetExtraBrightnessUseCase()
    private val setUseCase = SetExtraBrightnessUseCase()

    override suspend fun getEnabled(): ApiResult<Boolean> = getUseCase()
    override suspend fun setEnabled(enabled: Boolean): ApiResult<Unit> = setUseCase(enabled)
}