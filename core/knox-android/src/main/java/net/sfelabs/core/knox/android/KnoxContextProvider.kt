package net.sfelabs.core.knox.android

import android.content.Context

interface KnoxContextProvider {
    fun getContext(): Context

    companion object {
        private var instance: KnoxContextProvider? = null

        fun init(provider: KnoxContextProvider) {
            instance = provider
        }

        fun get(): Context = instance?.getContext()
            ?: throw IllegalStateException("KnoxContextProvider not initialized")
    }
}