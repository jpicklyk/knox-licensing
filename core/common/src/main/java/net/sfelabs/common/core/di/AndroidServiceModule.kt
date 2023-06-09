package net.sfelabs.common.core.di

import android.content.Context
import android.net.wifi.WifiManager
import androidx.core.content.getSystemService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AndroidServiceModule {

    @Provides
    @Singleton
    fun provideWifiManager(context: Context) = context.getSystemService<WifiManager>()!!
}