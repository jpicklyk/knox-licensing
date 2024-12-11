package net.sfelabs.core.knox.feature.processor.model

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.internal.component.StateMapping

data class FeatureMetadata(
    val name: String,
    val description: String,
    val category: FeatureCategory,
    val stateMapping: StateMapping,
    var configType: KSType? = null,
    var getter: KSClassDeclaration? = null,
    var setter: KSClassDeclaration? = null
)