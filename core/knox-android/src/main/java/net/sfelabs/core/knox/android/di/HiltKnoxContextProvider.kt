package net.sfelabs.core.knox.android.di

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import net.sfelabs.core.knox.android.KnoxContextProvider
import javax.inject.Inject

class HiltKnoxContextProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : KnoxContextProvider {
    override fun getContext(): Context = context
}