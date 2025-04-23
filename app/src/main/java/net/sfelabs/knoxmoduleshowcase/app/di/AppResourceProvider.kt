package net.sfelabs.knoxmoduleshowcase.app.di

import android.content.Context
import net.sfelabs.knox_enterprise.api.ResourceProvider
import javax.inject.Inject

class AppResourceProvider @Inject constructor(
    private val context: Context
) : ResourceProvider {
    override fun getString(resId: Int): String {
        return context.getString(resId)
    }

    override fun getString(resId: Int, vararg formatArgs: Any): String {
        return context.getString(resId, *formatArgs)
    }
}