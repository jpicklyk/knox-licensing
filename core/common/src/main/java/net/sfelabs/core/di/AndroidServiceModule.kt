package net.sfelabs.core.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.PowerManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AndroidServiceModule {

    @Provides
    @Singleton
    fun provideWifiManager(@ApplicationContext context: Context) = context.getSystemService(Context.WIFI_SERVICE)!! as WifiManager

    @Provides
    @Singleton
    fun provideConnectivityManager(@ApplicationContext context: Context) =
        context.getSystemService(Context.CONNECTIVITY_SERVICE)!! as ConnectivityManager

    @Provides
    @Singleton
    fun providePowerManager(@ApplicationContext context: Context) =
        context.getSystemService(Context.POWER_SERVICE)!! as PowerManager
}