package net.sfelabs.knox_tactical.domain.policy

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.FeatureDefinition
import net.sfelabs.core.knox.feature.api.BooleanPolicy
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.knox_tactical.domain.use_cases.calling.GetAutoRecordCallEnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.calling.SetAutoRecordCallEnabledUseCase

@FeatureDefinition(
    title = "Auto Record Calls",
    description = "Automatically record all phone calls when enabled.",
    category = FeatureCategory.Toggle,
    stateMapping = StateMapping.DIRECT
)
class AutoRecordCallPolicy : BooleanPolicy() {
    private val getUseCase = GetAutoRecordCallEnabledUseCase()
    private val setUseCase = SetAutoRecordCallEnabledUseCase()

    override suspend fun getEnabled(): ApiResult<Boolean> = getUseCase()
    override suspend fun setEnabled(enabled: Boolean): ApiResult<Unit> = setUseCase(enabled)
}