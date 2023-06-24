package net.sfelabs.common.core.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.PowerManager
import androidx.core.content.getSystemService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sfelabs.common.core.services.NetworkConnectivityService
import net.sfelabs.common.core.services.internals.EthernetNetworkService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AndroidServiceModule {

    @Provides
    @Singleton
    fun provideWifiManager(@ApplicationContext context: Context) = context.getSystemService<WifiManager>()!!

    @Provides
    @Singleton
    fun provideConnectivityManager(@ApplicationContext context: Context) =
        context.getSystemService<ConnectivityManager>()!!

    @Provides
    @Singleton
    fun providePowerManager(@ApplicationContext context: Context) =
        context.getSystemService<PowerManager>()!!

    @Provides
    @Singleton
    fun provideNetworkService(connectivityManager: ConnectivityManager): NetworkConnectivityService {
        return EthernetNetworkService(connectivityManager)
    }
}