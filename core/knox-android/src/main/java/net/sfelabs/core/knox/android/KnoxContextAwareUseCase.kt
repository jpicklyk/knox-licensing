package net.sfelabs.core.knox.android

import android.content.Context
import net.sfelabs.core.knox.api.domain.CoroutineApiUseCase

abstract class KnoxContextAwareUseCase<in P, out R : Any> : CoroutineApiUseCase<P, R>() {
    protected val knoxContext: Context
        get() = KnoxContextProvider.get()
}