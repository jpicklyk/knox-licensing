package net.sfelabs.core.knox.feature.processor.model

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import net.sfelabs.core.knox.feature.api.FeatureCategory

data class ProcessedFeature(
    val className: String,
    val packageName: String,
    val title: String,
    val description: String,
    val category: FeatureCategory,
    val valueType: KSType,
    val configType: KSType?,
    val declaration: KSClassDeclaration
) {
    val isConfigurable: Boolean get() = configType != null
}
