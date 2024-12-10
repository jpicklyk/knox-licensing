package net.sfelabs.core.knox.feature.processor.model

enum class PackageName(val value: String) {
    FEATURE_COMPONENT("net.sfelabs.core.knox.feature.domain.component"),
    FEATURE_MODEL("net.sfelabs.core.knox.feature.domain.model"),
    FEATURE_REGISTRY("net.sfelabs.core.knox.feature.domain.registry"),
    FEATURE_HANDLER("net.sfelabs.core.knox.feature.domain.handler"),
    FEATURE_HILT("net.sfelabs.core.knox.feature.hilt")

}