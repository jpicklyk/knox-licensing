package net.sfelabs.common.core.model

sealed class NetworkStatus {
    object Unknown: NetworkStatus() {
        override fun toString(): String {
            return "Unknown"
        }
    }
    object Connected: NetworkStatus() {
        override fun toString(): String {
            return "Connected"
        }
    }
    object Disconnected: NetworkStatus() {
        override fun toString(): String {
            return "Disconnected"
        }
    }
}