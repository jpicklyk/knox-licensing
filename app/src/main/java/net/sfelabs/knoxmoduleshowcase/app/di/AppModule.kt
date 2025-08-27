package net.sfelabs.knoxmoduleshowcase.app.di

import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sfelabs.knox_enterprise.api.ResourceProvider
import net.sfelabs.knox_enterprise.license.domain.usecase.KnoxLicenseUseCase
import javax.inject.Singleton
import net.sfelabs.knox_tactical.generated.di.GeneratedModuleIndex
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.EthernetAidlUseCaseFactory
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.GetMacAddressViaAidlUseCase

@Module(includes = [GeneratedModuleIndex::class])
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideResources(@ApplicationContext context: Context): Resources {
        return context.resources
    }

    @Provides
    @Singleton
    fun provideResourceProvider(
        @ApplicationContext context: Context
    ): ResourceProvider {
        return AppResourceProvider(context)
    }
    
    @Provides
    @Singleton
    fun provideGetMacAddressViaAidlUseCase(
        @ApplicationContext context: Context
    ): GetMacAddressViaAidlUseCase {
        return EthernetAidlUseCaseFactory.createGetMacAddressUseCase(context)
    }
}