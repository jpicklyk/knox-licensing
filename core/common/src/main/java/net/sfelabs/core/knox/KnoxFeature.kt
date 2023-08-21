package net.sfelabs.core.knox

data class KnoxFeature(
    val name: String,
    val description: String,
    val knoxComponentType: KnoxComponentType = KnoxComponentType.BooleanComponent,
    var enabledState: Boolean
)