package net.sfelabs.core.knox.feature.processor.model

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import net.sfelabs.core.knox.feature.api.PolicyCategory

data class ProcessedPolicy(
    val className: String,
    val packageName: String,
    val title: String,
    val description: String,
    val category: PolicyCategory,
    val valueType: KSType,
    val configType: KSType?,
    val declaration: KSClassDeclaration
)
