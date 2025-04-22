package net.sfelabs.knox_tactical.domain.policy.pogo

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.PolicyDefinition
import net.sfelabs.core.knox.feature.api.BooleanStatePolicy
import net.sfelabs.core.knox.feature.api.PolicyCategory
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.knox_tactical.domain.use_cases.pogo.DisablePogoKeyboardConnectionUseCase
import net.sfelabs.knox_tactical.domain.use_cases.pogo.IsPogoKeyboardConnectionDisabledUseCase

@PolicyDefinition(
    title = "Disable POGO Keyboard Connection",
    description = "Disables the POGO pins which prevents a keyboard from being connected",
    category = PolicyCategory.Toggle
)
class DisablePogoKeyboardConnectionPolicy: BooleanStatePolicy(stateMapping = StateMapping.DIRECT) {
    private val getUseCase = IsPogoKeyboardConnectionDisabledUseCase()
    private val setUseCase = DisablePogoKeyboardConnectionUseCase()

    override suspend fun getEnabled(): ApiResult<Boolean> = getUseCase()
    override suspend fun setEnabled(enabled: Boolean): ApiResult<Unit> = setUseCase(enabled)
}