package net.sfelabs.core.knox.android

import android.content.Context

interface WithAndroidApplicationContext {
    val applicationContext: Context
        get() = AndroidApplicationContextProvider.get()
}