package net.sfelabs.knoxmoduleshowcase.features.tactical.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.sfelabs.android_log_wrapper.Log
import net.sfelabs.core.knox.KnoxFeature
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.knoxmoduleshowcase.features.tactical.presentation.TacticalKnoxState
import javax.inject.Inject

@HiltViewModel
class TacticalTesterViewModel @Inject constructor(
    private val setTacticalDeviceModeUseCase: net.sfelabs.knox_tactical.domain.use_cases.tdm.SetTacticalDeviceModeUseCase,
    private val getTacticalDeviceModeUseCase: net.sfelabs.knox_tactical.domain.use_cases.tdm.GetTacticalDeviceModeUseCase,
    private val setAutoTouchSensitivityUseCase: net.sfelabs.knox_tactical.domain.use_cases.auto_touch.SetAutoTouchSensitivityUseCase,
    private val getAutoTouchSensitivityUseCase: net.sfelabs.knox_tactical.domain.use_cases.auto_touch.GetAutoTouchSensitivityUseCase,
    private val log: Log
): ViewModel(){
    private val _state = MutableStateFlow(TacticalKnoxState(isLoading = true))
    val state = _state.asStateFlow()

    private val _knoxFeatureList = MutableStateFlow<List<KnoxFeature>>(emptyList())
    val knoxFeatureList = _knoxFeatureList.asStateFlow()

    init {
        viewModelScope.launch {
            getTacticalModeState()
            getAutoTouchSensitivityState()
            _state.update {it.copy(isLoading = false)}
        }
    }

    fun onEvent(event: TacticalKnoxEvents) {
        viewModelScope.launch {
            when(event) {
                TacticalKnoxEvents.GetTacticalDeviceMode ->
                    getTacticalModeState()
                is TacticalKnoxEvents.SetTacticalDeviceMode ->
                    setTacticalModeState(event.enable)
                TacticalKnoxEvents.GetAutoTouchSensitivity ->
                    getAutoTouchSensitivityState()
                is TacticalKnoxEvents.SetAutoTouchSensitivity ->
                    setAutoTouchSensitivityState(event.enable)

                is TacticalKnoxEvents.FeatureChanged -> {
                    val updatedList = _knoxFeatureList.value.toMutableList()
                    val feature = updatedList.find { it.name == event.featureName }

                    feature?.enabledState = event.isEnabled
                    _knoxFeatureList.value = updatedList
                    TODO("set whatever the feature is in Knox Use Case")
                }
            }
        }
    }

    private suspend fun getTacticalModeState() {
        when (val apiCall = getTacticalDeviceModeUseCase()) {
            is ApiCall.Success -> {
                _state.update {it.copy(
                    isTacticalDeviceModeEnabled = apiCall.data
                )}
            }
            is ApiCall.Error -> {
                log.d("An error occurred")
            }
            is ApiCall.NotSupported -> {
                log.e("getTacticalModeState method is not supported")
            }
        }
    }

    private suspend fun setTacticalModeState(enable: Boolean) {
        when (setTacticalDeviceModeUseCase(enable)) {
            is ApiCall.Success -> {
                _state.update {it.copy(isTacticalDeviceModeEnabled = enable)}
            }
            is ApiCall.Error -> {
                log.d("An error occurred")
            }
            is ApiCall.NotSupported -> {
                log.e("setTacticalModeState method is not supported")
            }
        }
    }

    private suspend fun getAutoTouchSensitivityState() {
        when (val apiCall = getAutoTouchSensitivityUseCase()) {
            is ApiCall.Success -> {
                _state.update {it.copy(
                    isAutoTouchSensitivityEnabled = apiCall.data
                )}
            }
            is ApiCall.Error -> {
                log.d("An error occurred")
            }
            is ApiCall.NotSupported -> {
                log.e("getAutoTouchSensitivityState method is not supported")
            }
        }
    }

    private suspend fun setAutoTouchSensitivityState(enable: Boolean) {
        when (setAutoTouchSensitivityUseCase(enable)) {
            is ApiCall.Success -> {
                _state.update {it.copy(isAutoTouchSensitivityEnabled = enable)}
            }
            is ApiCall.Error -> {
                log.d("An error occurred")
            }
            is ApiCall.NotSupported -> {
                log.e("setAutoTouchSensitivityState method is not supported")
            }
        }
    }

}