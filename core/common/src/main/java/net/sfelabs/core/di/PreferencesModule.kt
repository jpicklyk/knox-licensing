package net.sfelabs.core.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sfelabs.core.data.datasource.DataStoreSource
import net.sfelabs.core.data.datasource.DataStoreSourceImpl
import net.sfelabs.core.domain.repository.PreferencesRepository
import net.sfelabs.core.domain.repository.PreferencesRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    @Singleton
    fun providePreferencesRepository(dataSource: DataStoreSource): PreferencesRepository {
        return PreferencesRepositoryImpl(dataSource)
    }

    @Provides
    @Singleton
    fun provideDataStoreDataSource(@ApplicationContext context: Context): DataStoreSource {
        return DataStoreSourceImpl(context)
    }

}