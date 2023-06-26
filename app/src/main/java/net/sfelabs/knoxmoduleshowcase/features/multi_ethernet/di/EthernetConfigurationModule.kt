package net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.di

import android.net.ConnectivityManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class EthernetConfigurationModule {
    @Provides
    @Singleton
    fun provideNetworkService(connectivityManager: ConnectivityManager): net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.services.NetworkConnectivityService {
        return net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.services.internals.EthernetNetworkService(
            connectivityManager
        )
    }
}