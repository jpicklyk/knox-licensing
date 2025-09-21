package net.sfelabs.knoxmoduleshowcase.di

import android.content.Context
import com.github.jpicklyk.knox.licensing.KnoxLicenseFactory
import com.github.jpicklyk.knox.licensing.domain.KnoxLicenseHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object KnoxLicensingModule {

    @Provides
    @Singleton
    fun provideKnoxLicenseHandler(@ApplicationContext context: Context): KnoxLicenseHandler {
        return KnoxLicenseFactory.createFromBuildConfig(context)
    }
}