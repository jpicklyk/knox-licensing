package net.sfelabs.knoxmoduleshowcase.features.network_manager.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.service.NetworkMonitorService

@Module
@InstallIn(ServiceComponent::class)
abstract class NetworkMonitorServiceModule {

    @Binds
    @ServiceScoped
    abstract fun bindNetworkMonitorService(networkMonitorService: NetworkMonitorService): NetworkMonitorService

}