package net.sfelabs.knoxmoduleshowcase.di

import android.content.Context
import com.github.jpicklyk.knox.licensing.KnoxLicenseFactory
import com.github.jpicklyk.knox.licensing.domain.KnoxLicenseHandler
import com.github.jpicklyk.knox.licensing.domain.LicenseSelectionStrategy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sfelabs.knoxmoduleshowcase.BuildConfig
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object KnoxLicensingModule {

    @Provides
    @Singleton
    fun provideLicenseSelectionStrategy(): LicenseSelectionStrategy {
        return TacticalDeviceLicenseSelectionStrategy()
    }

    @Provides
    @Singleton
    fun provideKnoxLicenseHandler(
        @ApplicationContext context: Context,
        licenseSelectionStrategy: LicenseSelectionStrategy
    ): KnoxLicenseHandler {
        return KnoxLicenseFactory.create(
            context,
            licenseSelectionStrategy,
            BuildConfig.KNOX_LICENSE_KEY,
            BuildConfig.KNOX_LICENSE_KEYS
        )
    }
}