package net.sfelabs.core

sealed class UiText {
    abstract override fun toString(): String

    data class DynamicString(val value: String) : UiText() {
        override fun toString(): String = value
    }

    class StringResource(
        @androidx.annotation.StringRes val resId: Int,
        vararg val args: Any,
        private val fallbackString: String? = null
    ) : UiText() {
        override fun toString(): String = fallbackString ?: "StringResource(resId=$resId)"
    }

    @androidx.compose.runtime.Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> androidx.compose.ui.res.stringResource(
                id = resId,
                formatArgs = args
            )
        }
    }

    fun asString(context: android.content.Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(resId, *args)
        }
    }

    companion object {
        fun resourceString(@androidx.annotation.StringRes resId: Int, vararg args: Any, fallback: String? = null): UiText =
            StringResource(resId, *args, fallbackString = fallback)
    }
}