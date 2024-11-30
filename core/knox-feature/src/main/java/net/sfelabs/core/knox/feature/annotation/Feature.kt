package net.sfelabs.core.knox.feature.annotation

import net.sfelabs.core.knox.feature.domain.model.FeatureCategory

@Target(AnnotationTarget.FILE)
annotation class Feature(
    val name: String,
    val description: String,
    val category: FeatureCategory
)
