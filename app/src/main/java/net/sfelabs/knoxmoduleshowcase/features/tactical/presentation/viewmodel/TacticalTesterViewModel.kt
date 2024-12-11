package net.sfelabs.knoxmoduleshowcase.features.tactical.presentation.viewmodel

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.sfelabs.android_log_wrapper.Log
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.feature.internal.model.old.KnoxFeatureState
import net.sfelabs.core.knox.feature.internal.model.old.KnoxFeatureValueType
import net.sfelabs.core.knox.feature.processFeatureList
import net.sfelabs.knox_tactical.domain.model.TacticalFeature
import net.sfelabs.knox_tactical.domain.services.TacticalFeatureService
import net.sfelabs.knoxmoduleshowcase.R
import net.sfelabs.knoxmoduleshowcase.features.tactical.presentation.TacticalKnoxState
import javax.inject.Inject

@HiltViewModel
class TacticalTesterViewModel @Inject constructor(
    private val resources: Resources,
    private val log: Log,
    private val featureService: TacticalFeatureService
): ViewModel(){
    private val _state = MutableStateFlow(TacticalKnoxState(isLoading = true))
    val state = _state.asStateFlow()

    private val _knoxFeatureList = MutableStateFlow<List<KnoxFeatureState>>(emptyList())
    val knoxFeatureList = _knoxFeatureList.asStateFlow()

    init {
        viewModelScope.launch {
            loadFeaturesFromJson()
            _state.update {it.copy(isLoading = false)}
        }
    }

    fun onEvent(event: TacticalKnoxEvents) {
        viewModelScope.launch {
            when(event) {
                is TacticalKnoxEvents.FeatureOnOffChanged -> {
                    val updatedList = _knoxFeatureList.value.toMutableList()
                    val feature = updatedList.find { it.key == event.featureKey }
                    val index = updatedList.indexOf(feature)
                    updatedList[index] = updateFeature(feature!!, event.isEnabled, event.data)
                    _knoxFeatureList.update {
                        updatedList
                    }
                }

                is TacticalKnoxEvents.FeatureIntegerValueChanged -> {
                    val updatedList = _knoxFeatureList.value.toMutableList()
                    val feature = updatedList.find { it.key == event.featureKey }
                    val index = updatedList.indexOf(feature)
                    updatedList[index] = feature!!.copy(
                        knoxFeatureValueType = KnoxFeatureValueType.StringValue(
                        event.featureValue
                    )
                    )
                    _knoxFeatureList.update {
                        updatedList
                    }
                }
            }
        }
    }

    /**
     * Read the knox features from the json file and for each feature check for current state
     * and convert KnoxFeature to KnoxFeatureState object.
     */
    private suspend fun loadFeaturesFromJson() {
        val featureList =
            processFeatureList(resources.openRawResource(R.raw.tactical_features))

        _knoxFeatureList.update {
            featureList.map {knoxFeature ->
                val state = KnoxFeatureState(
                    key = knoxFeature.key,
                    title = knoxFeature.title,
                    description = knoxFeature.description
                )
                val feature = TacticalFeature(knoxFeature.key)!!
                when(val apiCall = featureService.getApiEnabledState(feature)) {
                    ApiResult.NotSupported -> {
                        state.copy(isSupported = false)
                    }

                    is ApiResult.Error -> {
                        log.e(apiCall.apiError.message)
                        state.copy(hasError = true, error = apiCall.apiError.message)
                    }

                    is ApiResult.Success -> {
                        when(val value = apiCall.data.value) {
                            is String -> KnoxFeatureValueType.StringValue(
                                value
                            )
                            is Boolean -> KnoxFeatureValueType.BooleanValue(
                                value
                            )
                            is Int -> KnoxFeatureValueType.IntegerValue(
                                value
                            )
                            else -> KnoxFeatureValueType.NoValue
                        }
                        state.copy(
                            enabled = apiCall.data.enabled,
                            knoxFeatureValueType = featureService.getFeatureValueType(
                                feature, apiCall.data.value
                            )
                        )
                    }
                }

            }


        }

    }

    private suspend fun updateFeature(state: KnoxFeatureState, enabled: Boolean, data: Any?): KnoxFeatureState {

        return when (
            val apiCall = featureService.setCurrentState(TacticalFeature(state.key), enabled, data)
        ) {
            ApiResult.NotSupported -> {
                state.copy(isSupported = false)
            }

            is ApiResult.Error -> {
                log.e(apiCall.apiError.message)
                state.copy(hasError = true, error = apiCall.apiError.message)
            }

            is ApiResult.Success -> {
                state.copy(enabled = enabled)
            }
        }
    }


}