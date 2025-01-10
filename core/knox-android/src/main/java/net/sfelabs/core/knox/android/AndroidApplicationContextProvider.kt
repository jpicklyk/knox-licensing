package net.sfelabs.core.knox.android

import android.content.Context

interface AndroidApplicationContextProvider {
    fun getContext(): Context

    companion object {
        private var instance: AndroidApplicationContextProvider? = null

        fun init(provider: AndroidApplicationContextProvider) {
            instance = provider
        }

        fun get(): Context = instance?.getContext()
            ?: throw IllegalStateException("AndroidApplicationContextProvider not initialized")
    }
}