package net.sfelabs.core.knox.feature.processor.model

enum class PackageName(val value: String) {
    FEATURE_PUBLIC("net.sfelabs.core.knox.feature.api"),
    FEATURE_MODEL("net.sfelabs.core.knox.feature.domain.model"),
    FEATURE_REGISTRY("net.sfelabs.core.knox.feature.domain.registry"),
    FEATURE_HANDLER("net.sfelabs.core.knox.feature.domain.usecase.handler"),
    FEATURE_HILT("net.sfelabs.core.knox.feature.hilt"),
    API_DOMAIN_MODEL("net.sfelabs.core.domain.usecase.model")
}