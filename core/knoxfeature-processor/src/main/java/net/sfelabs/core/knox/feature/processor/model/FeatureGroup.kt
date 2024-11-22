package net.sfelabs.core.knox.feature.processor.model

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSType
import net.sfelabs.core.knox.feature.domain.model.FeatureCategory

data class FeatureGroup(
    val featureName: String,
    val category: FeatureCategory,
    val configType: KSType,
    var getter: KSAnnotated? = null,
    var setter: KSAnnotated? = null
)