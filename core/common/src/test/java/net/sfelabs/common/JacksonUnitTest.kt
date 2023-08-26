package net.sfelabs.common

import net.sfelabs.core.domain.model.knox.KnoxFeature
import net.sfelabs.core.domain.model.knox.KnoxFeatureValueType
import net.sfelabs.core.domain.processJson
import net.sfelabs.core.domain.provideJson
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class JacksonUnitTest {
    @Test
    fun test_BooleanComponent() {
        val json = "{\"key\":\"TacticalDeviceMode\",\"title\":\"Tactical Device Mode\",\"description\":\"Some description for the feature\",\"knoxFeatureValueType\":{\"@type\":\"KnoxFeatureValueType\$NoValue\"},\"enabled\":true}"
        val feature = KnoxFeature(
            key = "TacticalDeviceMode",
            title = "Tactical Device Mode",
            description = "Some description for the feature",
            enabled = true,
            knoxFeatureValueType = KnoxFeatureValueType.NoValue
            )

        val jsonResult = provideJson(feature)
        val featureResult = processJson(json)
        println("Output: $jsonResult")
        assert(jsonResult == json)
        assert(
            featureResult.key == "TacticalDeviceMode"
                    && featureResult.title == "Tactical Device Mode"
                    && featureResult.description == "Some description for the feature"
                    && featureResult.enabled
                    && featureResult.knoxFeatureValueType is KnoxFeatureValueType.NoValue
        )
    }

    @Test
    fun test_SpinnerComponent() {
        val feature = KnoxFeature(
            key = "LteBand",
            title = "LTE Band Locking",
            description = "Lock the LTE band to the value specified provided the HW supports the band passed",
            enabled = false,
            knoxFeatureValueType = KnoxFeatureValueType.IntegerValue(78)
        )

        val jsonResult = provideJson(feature)
        println(jsonResult)
    }
}