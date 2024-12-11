package net.sfelabs.core.knox.feature.internal.model.old

data class KnoxFeature(
    val key: String,
    val title: String,
    val description: String,
    val enabled: Boolean = false,
    val knoxFeatureValueType: KnoxFeatureValueType<*> = KnoxFeatureValueType.NoValue,
    )