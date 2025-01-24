package net.sfelabs.knox_tactical.domain.model

sealed class AutoCallPickupMode(val value: Int, val displayName: String) {
    companion object {
        val values: List<AutoCallPickupMode> by lazy {
            listOf(Disable, Enable, EnableAlwaysAccept)
        }

        operator fun invoke(type: Int): AutoCallPickupMode {
            return when(type) {
                0 -> Disable
                1 -> Enable
                else -> EnableAlwaysAccept
            }
        }

        fun fromValue(value: Int): AutoCallPickupMode {
            return when(value) {
                0 -> Disable
                1 -> Enable
                2 -> EnableAlwaysAccept
                else -> throw IllegalArgumentException("Invalid AutoCallPickupMode value: $value")
            }
        }

        fun fromDisplayName(displayName: String): AutoCallPickupMode {
            return when(displayName) {
                "Disabled" -> Disable
                "Enabled" -> Enable
                "Enabled Always Accept" -> EnableAlwaysAccept
                else -> throw IllegalArgumentException("Invalid AutoCallPickupMode displayName: $displayName")
            }
        }
    }

    data object Disable : AutoCallPickupMode(0, "Disabled")
    data object Enable : AutoCallPickupMode(1, "Enabled")
    data object EnableAlwaysAccept : AutoCallPickupMode(2, "Enabled Always Accept")

    override fun toString(): String = displayName
}