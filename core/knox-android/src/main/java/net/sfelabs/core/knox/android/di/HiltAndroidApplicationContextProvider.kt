package net.sfelabs.core.knox.android.di

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import net.sfelabs.core.knox.android.AndroidApplicationContextProvider
import javax.inject.Inject

class HiltAndroidApplicationContextProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : AndroidApplicationContextProvider {
    override fun getContext(): Context = context
}