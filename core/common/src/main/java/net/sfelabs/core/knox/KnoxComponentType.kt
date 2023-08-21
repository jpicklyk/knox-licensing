package net.sfelabs.core.knox

import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
sealed class KnoxComponentType {
    object BooleanComponent: KnoxComponentType()
    data class SpinnerComponent(
        val lteBandState: Int = 0
    ): KnoxComponentType()

}
