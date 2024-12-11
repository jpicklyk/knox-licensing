package net.sfelabs.core.knox.feature.internal.model.old

data class KnoxFeatureState(
    val key: String,
    val title: String,
    val description: String,
    val knoxFeatureValueType: KnoxFeatureValueType<Any> = KnoxFeatureValueType.NoValue,
    val isSupported: Boolean = true,
    val enabled: Boolean = false,
    val hasError: Boolean = false,
    val error: String? = null
)
