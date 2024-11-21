package net.sfelabs.core.knox.feature.domain.model.old

data class KnoxFeature(
    val key: String,
    val title: String,
    val description: String,
    val enabled: Boolean = false,
    val knoxFeatureValueType: KnoxFeatureValueType<*> = KnoxFeatureValueType.NoValue,
    )