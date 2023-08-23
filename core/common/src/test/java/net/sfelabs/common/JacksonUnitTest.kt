package net.sfelabs.common

import net.sfelabs.core.knox.KnoxComponentType
import net.sfelabs.core.knox.KnoxFeature
import net.sfelabs.core.knox.processJson
import net.sfelabs.core.knox.provideJson
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class JacksonUnitTest {
    @Test
    fun addition_isCorrect() {
        val json = "{\"name\":\"Tactical Device Mode\",\"description\":\"Some description for the feature\",\"knoxComponentType\":{\"@type\":\"KnoxComponentType\$BooleanComponent\"},\"enabledState\":true}"
        val feature = KnoxFeature(
            name = "Tactical Device Mode",
            description = "Some description for the feature",
            enabledState = true,
            knoxComponentType = KnoxComponentType.BooleanComponent
            )

        val jsonResult = provideJson(feature)
        val featureResult = processJson(json)
        println("Output: $jsonResult")
        assert(jsonResult == json)
        assert(
            featureResult.name == "Tactical Device Mode"
                    && featureResult.description == "Some description for the feature"
                    && featureResult.enabledState
                    && featureResult.knoxComponentType is KnoxComponentType.BooleanComponent
        )
    }
}