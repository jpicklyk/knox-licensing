package net.sfelabs.core.knox.feature.internal.component

enum class StateMapping {
    DIRECT,     // enabled = value
    INVERTED,   // enabled = !value
    CUSTOM      // Will use companion object's mapState function
}