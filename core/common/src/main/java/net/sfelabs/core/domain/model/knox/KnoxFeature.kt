package net.sfelabs.core.domain.model.knox

data class KnoxFeature(
    val key: String,
    val title: String,
    val description: String,
    val knoxFeatureValueType: KnoxFeatureValueType = KnoxFeatureValueType.BooleanValue,
    val enabled: Boolean = false,
    )