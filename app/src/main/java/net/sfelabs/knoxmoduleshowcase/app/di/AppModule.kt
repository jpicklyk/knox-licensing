package net.sfelabs.knoxmoduleshowcase.app.di

import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sfelabs.core.knox.feature.domain.registry.DefaultFeatureRegistry
import net.sfelabs.core.knox.feature.domain.registry.FeatureRegistry
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideResources(@ApplicationContext context: Context): Resources {
        return context.resources
    }

    @Provides
    @Singleton
    fun provideFeatureRegistry() : FeatureRegistry {
        return DefaultFeatureRegistry()
    }

}