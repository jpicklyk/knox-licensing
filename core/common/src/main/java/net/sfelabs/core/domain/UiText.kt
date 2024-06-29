package net.sfelabs.core.domain

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class UiText {
    abstract override fun toString(): String

    data class DynamicString(val value: String) : UiText() {
        override fun toString(): String = value
    }

    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any,
        private val fallbackString: String? = null
    ) : UiText() {
        override fun toString(): String = fallbackString ?: "StringResource(resId=$resId)"
    }

    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(id = resId, formatArgs = args)
        }
    }

    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(resId, *args)
        }
    }

    companion object {
        fun resourceString(@StringRes resId: Int, vararg args: Any, fallback: String? = null): UiText =
            StringResource(resId, *args, fallbackString = fallback)
    }
}