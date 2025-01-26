package net.sfelabs.core.knox.feature.annotation

import net.sfelabs.core.knox.feature.api.PolicyCategory

@Target(AnnotationTarget.CLASS)
annotation class PolicyDefinition(
    val title: String,
    val description: String,
    val category: PolicyCategory
)
