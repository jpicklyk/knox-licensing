package net.sfelabs.core.knox.feature.ui.model

sealed interface ConfigurationOption {
    val key: String
    val label: String

    data class Toggle(
        override val key: String,
        override val label: String,
        val isEnabled: Boolean
    ) : ConfigurationOption

    data class Choice(
        override val key: String,
        override val label: String,
        val selected: String,
        val options: List<String>
    ) : ConfigurationOption

    data class NumberInput(
        override val key: String,
        override val label: String,
        val value: Int,
        val range: IntRange? = null
    ) : ConfigurationOption
}
