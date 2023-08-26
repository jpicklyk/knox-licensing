package net.sfelabs.core.domain.model.knox

data class KnoxFeature(
    val key: String,
    val title: String,
    val description: String,
    val knoxFeatureValueType: KnoxFeatureValueType<Any> = KnoxFeatureValueType.NoValue,
    val enabled: Boolean = false,
    )