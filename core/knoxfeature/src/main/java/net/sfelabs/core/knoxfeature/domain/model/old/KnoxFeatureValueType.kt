package net.sfelabs.core.knoxfeature.domain.model.old

import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
sealed class KnoxFeatureValueType<out T: Any> {
    abstract val value: T

    data object NoValue: KnoxFeatureValueType<Nothing>() {
        override val value: Nothing

            get() = throw IllegalStateException("Cannot call getValue() on NoValue type!")
    }

    data class BooleanValue(override val value: Boolean): KnoxFeatureValueType<Boolean>()
    data class IntegerValue(override val value: Int): KnoxFeatureValueType<Int>()
    data class StringValue(override val value: String): KnoxFeatureValueType<String>()
}
