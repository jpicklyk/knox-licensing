package net.sfelabs.knoxmoduleshowcase.features.about

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

import javax.inject.Inject


interface TacticalEditionSoftware {
    val buildNumber: String
    val versionName: String
}

enum class TacticalEditionGen2(
    override val buildNumber: String,
    override val versionName: String
) : TacticalEditionSoftware {
    ANDROID10_GA("G981U1UEU1ATF8_B2BF", "Android 10, GA"),
    ANDROID10_MR1("G981U1UEU1ATL3_B2BF", "Android 10, MR1"),
    ANDROID10_MR2("G981U1UEU1AUG2_B2BF", "Android 10, MR2"),
    ANDROID10_MR3("G981U1UEU1AVB1_B2BF", "Android 10, MR3")
}

enum class TacticalEditionGen2Extension(
    override val buildNumber: String,
    override val versionName: String
) : TacticalEditionSoftware {
    ANDROID10_MR4("G981U1UES2AVF2_B2BF", "Android 10, MR4"),
    ANDROID10_MR4_SPECIAL_1("G981U1UES3AVF2_B2BF", "Android 10, MR4 (Special 1)"),
    ANDROID10_MR4_SPECIAL_2("G981U1UES4AWC1_B2BF", "Android 10, MR4 (Special 2)"),
    ANDROID11_GA("G981U1UEU3BVK1_B2BF", "Android 11, GA"),
    ANDROID11_MR1("G981U1UEU4BWC2_B2BF", "Android 11, MR1"),
    ANDROID11_MR2("G981U1UEU4BWI1_B2BF", "Android 11, MR2")
}

enum class TacticalEditionGen3(
    override val buildNumber: String,
    override val versionName: String
) : TacticalEditionSoftware {
    S23_ANDROID13_GA("S911U1UEU1AWH5_B2BF", "Android 13, GA"),
    XC6P_ANDROID13_GA("G736U1UEU4CWH5_B2BF", "Android 13, GA"),
}


class AboutScreenViewModel @Inject constructor(


): ViewModel() {

    private val _state = mutableStateOf(InformationState())
    val informationState: State<InformationState> = _state

    init {
        val gen2NormalList = TacticalEditionGen2::class.java.enumConstants?.toList()
        val gen2ExtensionList = TacticalEditionGen2Extension::class.java.enumConstants?.toList()
        val gen3NormalList = TacticalEditionGen3::class.java.enumConstants?.toList()
        _state.value = gen2NormalList?.let { _state.value.copy(gen2SoftwareList = it) }!!
        _state.value = gen2ExtensionList?.let {
            _state.value.copy(gen2ExtensionSoftwareList = it)
        }!!
        _state.value = gen3NormalList?.let {
            _state.value.copy(gen3SoftwareList = it, isLoaded = true)
        }!!
    }


}