package net.sfelabs.core.domain.model.knox

import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
sealed class KnoxFeatureValueType<out T: Any> {
    object NoValue: KnoxFeatureValueType<Nothing>()
    data class BooleanValue(val value: Boolean): KnoxFeatureValueType<Boolean>()
    data class IntegerValue(val value: Int): KnoxFeatureValueType<Int>()

}
