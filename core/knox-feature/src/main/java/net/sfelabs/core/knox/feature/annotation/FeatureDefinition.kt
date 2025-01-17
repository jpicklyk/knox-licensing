package net.sfelabs.core.knox.feature.annotation

import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.core.knox.feature.api.FeatureCategory

@Target(AnnotationTarget.CLASS)
annotation class FeatureDefinition(
    val title: String,
    val description: String,
    val category: FeatureCategory,
    val stateMapping: StateMapping = StateMapping.DIRECT
)
