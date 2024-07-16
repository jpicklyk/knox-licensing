package net.sfelabs.core.presentation

import android.content.Context
import androidx.annotation.StringRes

internal class ResourceProviderImpl (
    private val context: Context
) : ResourceProvider {
    override fun getString(@StringRes resId: Int): String {
        return context.getString(resId)
    }

    override fun getString(@StringRes resId: Int, vararg formatArgs: Any): String {
        return context.getString(resId, *formatArgs)
    }
}