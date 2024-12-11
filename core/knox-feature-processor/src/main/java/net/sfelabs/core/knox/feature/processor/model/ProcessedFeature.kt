package net.sfelabs.core.knox.feature.processor.model

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import net.sfelabs.core.knox.feature.internal.component.StateMapping
import net.sfelabs.core.knox.feature.api.FeatureCategory

data class ProcessedFeature(
    val className: String,
    val packageName: String,
    val title: String,
    val description: String,
    val category: FeatureCategory,
    val stateMapping: StateMapping,
    val valueType: KSType,
    val declaration: KSClassDeclaration
)
