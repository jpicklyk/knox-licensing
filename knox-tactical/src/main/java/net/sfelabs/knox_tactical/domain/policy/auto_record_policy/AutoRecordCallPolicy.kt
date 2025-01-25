package net.sfelabs.knox_tactical.domain.policy.auto_record_policy

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.FeatureDefinition
import net.sfelabs.core.knox.feature.api.BooleanStatePolicy
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.knox_tactical.domain.use_cases.calling.GetAutoRecordCallEnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.calling.SetAutoRecordCallEnabledUseCase

@FeatureDefinition(
    title = "Auto Record Calls",
    description = "Automatically record all phone calls when enabled.",
    category = FeatureCategory.Toggle
)
class AutoRecordCallPolicy : BooleanStatePolicy() {
    private val getUseCase = GetAutoRecordCallEnabledUseCase()
    private val setUseCase = SetAutoRecordCallEnabledUseCase()

    override suspend fun getEnabled(): ApiResult<Boolean> = getUseCase()
    override suspend fun setEnabled(enabled: Boolean): ApiResult<Unit> = setUseCase(enabled)
}