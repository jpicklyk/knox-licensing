package net.sfelabs.core.knox.feature.annotation

import net.sfelabs.core.knox.feature.domain.model.FeatureCategory
import net.sfelabs.core.knox.feature.domain.component.StateMapping

@Target(AnnotationTarget.FILE)
annotation class Feature(
    val name: String,
    val description: String,
    val category: FeatureCategory,
    val stateMapping: StateMapping = StateMapping.DIRECT
)
