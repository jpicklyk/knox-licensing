package net.sfelabs.knox_tactical.domain.services

import net.sfelabs.android_log_wrapper.Log
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.core.domain.UiText
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.domain.model.knox.KnoxFeatureValueType
import net.sfelabs.knox_tactical.domain.model.TacticalFeature
import net.sfelabs.knox_tactical.domain.use_cases.auto_touch.GetAutoTouchSensitivityUseCase
import net.sfelabs.knox_tactical.domain.use_cases.auto_touch.SetAutoTouchSensitivityUseCase
import net.sfelabs.knox_tactical.domain.use_cases.hotspot.GetHotspot20StateUseCase
import net.sfelabs.knox_tactical.domain.use_cases.hotspot.SetHotspot20StateUseCase
import net.sfelabs.knox_tactical.domain.use_cases.lte.DisableBandLockingUseCase
import net.sfelabs.knox_tactical.domain.use_cases.lte.EnableBandLockingUseCase
import net.sfelabs.knox_tactical.domain.use_cases.lte.GetBandLockingStateUseCase
import net.sfelabs.knox_tactical.domain.use_cases.ramplus.GetRamPlusDisabledStateUseCase
import net.sfelabs.knox_tactical.domain.use_cases.ramplus.SetRamPlusStateUseCase
import net.sfelabs.knox_tactical.domain.use_cases.tdm.GetTacticalDeviceModeUseCase
import net.sfelabs.knox_tactical.domain.use_cases.tdm.SetTacticalDeviceModeUseCase
import net.sfelabs.knox_tactical.domain.use_cases.wifi.EnableRandomizedMacAddressUseCase
import net.sfelabs.knox_tactical.domain.use_cases.wifi.GetRandomizedMacAddressEnabledUseCase
import javax.inject.Inject

class TacticalFeatureService @Inject constructor(
    private val setTacticalDeviceModeUseCase: SetTacticalDeviceModeUseCase,
    private val getTacticalDeviceModeUseCase: GetTacticalDeviceModeUseCase,
    private val setAutoTouchSensitivityUseCase: SetAutoTouchSensitivityUseCase,
    private val getAutoTouchSensitivityUseCase: GetAutoTouchSensitivityUseCase,
    private val getHotspot20StateUseCase: GetHotspot20StateUseCase,
    private val setHotspot20StateUseCase: SetHotspot20StateUseCase,
    private val getRamPlusDisabledStateUseCase: GetRamPlusDisabledStateUseCase,
    private val setRamPlusStateUseCase: SetRamPlusStateUseCase,
    private val getRandomizedMacAddressEnabledUseCase: GetRandomizedMacAddressEnabledUseCase,
    private val enableRandomizedMacAddressUseCase: EnableRandomizedMacAddressUseCase,
    private val enableBandLockingUseCase: EnableBandLockingUseCase,
    private val disableBandLockingUseCase: DisableBandLockingUseCase,
    private val getBandLockingStateUseCase: GetBandLockingStateUseCase,
    private val log: Log
) {

    suspend fun getApiEnabledState(feature: TacticalFeature): ApiCall<Boolean> {
        return when(feature) {
            TacticalFeature.AutoSensitivity -> getAutoTouchSensitivityUseCase.isApiEnabled()
            TacticalFeature.TacticalDeviceMode -> getTacticalDeviceModeUseCase.isApiEnabled()
            TacticalFeature.Hotspot20 -> getHotspot20StateUseCase.isApiEnabled()
            TacticalFeature.RamPlus -> getRamPlusDisabledStateUseCase.isApiEnabled()
            TacticalFeature.RandomMac -> getRandomizedMacAddressEnabledUseCase.isApiEnabled()
            TacticalFeature.LteBandLock -> getBandLockingStateUseCase.isApiEnabled()
        }
    }

    suspend fun getApiValueState(feature: TacticalFeature): KnoxFeatureValueType<Any> {
        return when(feature) {
            TacticalFeature.AutoSensitivity -> KnoxFeatureValueType.BooleanValue(getAutoTouchSensitivityUseCase())
            TacticalFeature.TacticalDeviceMode -> getTacticalDeviceModeUseCase()
            TacticalFeature.Hotspot20 -> getHotspot20StateUseCase()
            TacticalFeature.RamPlus -> getRamPlusDisabledStateUseCase()
            TacticalFeature.RandomMac -> getRandomizedMacAddressEnabledUseCase()
            TacticalFeature.LteBandLock -> getBandLockingStateUseCase()
        }
    }

    suspend fun setCurrentState(feature: TacticalFeature?, enable: Boolean, data: Any? = null): UnitApiCall {
        if(feature == null)
            return ApiCall.Error(UiText.DynamicString("Feature key is not supported"))

        return when(feature) {
            TacticalFeature.AutoSensitivity -> setAutoTouchSensitivityUseCase(enable)
            TacticalFeature.TacticalDeviceMode -> setTacticalDeviceModeUseCase(enable)
            TacticalFeature.Hotspot20 -> setHotspot20StateUseCase(enable)
            TacticalFeature.RamPlus -> setRamPlusStateUseCase(enable)
            TacticalFeature.RandomMac ->enableRandomizedMacAddressUseCase(enable)
            TacticalFeature.LteBandLock -> {
                if(enable && data != null) enableBandLockingUseCase(data as Int)
                else disableBandLockingUseCase()
            }
        }
    }

}