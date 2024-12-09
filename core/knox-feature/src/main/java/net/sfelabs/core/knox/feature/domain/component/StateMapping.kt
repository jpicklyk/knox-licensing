package net.sfelabs.core.knox.feature.domain.component

enum class StateMapping {
    DIRECT,     // enabled = value
    INVERTED,   // enabled = !value
    CUSTOM      // Will use companion object's mapState function
}