package net.sfelabs.knox_tactical.domain.model

sealed class LteNrMode(val value: Int, val displayName: String) {
    companion object {
        val values: List<LteNrMode> by lazy {
            listOf(EnableBothSaAndNsa, DisableSa, DisableNsa)
        }

        operator fun invoke(type: Int): LteNrMode {
            return when(type) {
                1 -> DisableSa
                2 -> DisableNsa
                else -> EnableBothSaAndNsa
            }
        }

        fun fromValue(value: Int): LteNrMode {
            return when(value) {
                0 -> EnableBothSaAndNsa
                1 -> DisableSa
                2 -> DisableNsa
                else -> throw IllegalArgumentException("Invalid LteNrMode value: $value")
            }
        }

        fun fromDisplayName(displayName: String): LteNrMode {
            return when (displayName) {
                "Enable Both SA and NSA" -> EnableBothSaAndNsa
                "Disable SA" -> DisableSa
                "Disable NSA" -> DisableNsa
                else -> throw IllegalArgumentException("Invalid LteNrMode displayName: $displayName")
            }
        }
    }

    data object EnableBothSaAndNsa : LteNrMode(0, "Enable Both SA and NSA")
    data object DisableSa : LteNrMode(1, "Disable SA")
    data object DisableNsa : LteNrMode(2, "Disable NSA")

    override fun toString(): String = displayName
}