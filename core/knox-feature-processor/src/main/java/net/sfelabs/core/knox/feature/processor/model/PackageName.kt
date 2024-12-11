package net.sfelabs.core.knox.feature.processor.model

enum class PackageName(val value: String) {
    FEATURE_ANNOTATION("net.sfelabs.core.knox.feature.annotation"),
    FEATURE_PUBLIC("net.sfelabs.core.knox.feature.api"),
    FEATURE_COMPONENT("net.sfelabs.core.knox.feature.internal.component"),
    FEATURE_MODEL("net.sfelabs.core.knox.feature.internal.model"),
    FEATURE_REGISTRY("net.sfelabs.core.knox.feature.internal.registry"),
    FEATURE_HANDLER("net.sfelabs.core.knox.feature.internal.handler"),
    FEATURE_HILT("net.sfelabs.core.knox.feature.hilt"),
    API_DOMAIN("net.sfelabs.core.knox.api.domain")

}