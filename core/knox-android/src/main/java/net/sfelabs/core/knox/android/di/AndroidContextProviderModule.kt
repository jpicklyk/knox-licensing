package net.sfelabs.core.knox.android.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.sfelabs.core.knox.android.AndroidApplicationContextProvider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AndroidContextProviderModule {
    @Binds
    @Singleton
    abstract fun bindKnoxContextProvider(
        impl: HiltAndroidApplicationContextProvider
    ): AndroidApplicationContextProvider
}