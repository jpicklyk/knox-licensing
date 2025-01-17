package net.sfelabs.core.knox.feature.api

import net.sfelabs.core.knox.feature.domain.usecase.handler.FeatureHandler

interface FeatureComponent<T : PolicyState> {
    val featureName: String
    val title: String
    val description: String
    val category: FeatureCategory
    val handler: FeatureHandler<T>
    val defaultValue: T
    val key: FeatureKey<T>

}