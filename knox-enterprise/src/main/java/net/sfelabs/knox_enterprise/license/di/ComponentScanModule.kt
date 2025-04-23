package net.sfelabs.knox_enterprise.license.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sfelabs.knox_enterprise.license.data.KnoxErrorMapper
import net.sfelabs.knox_enterprise.license.data.LicenseRepositoryImpl
import net.sfelabs.knox_enterprise.license.domain.repository.LicenseRepository
import net.sfelabs.knox_enterprise.license.domain.usecase.GetLicenseInfoUseCase
import net.sfelabs.knox_enterprise.license.domain.usecase.GetLicenseInfoUseCaseImpl
import net.sfelabs.knox_enterprise.license.domain.usecase.KnoxLicenseUseCase
import net.sfelabs.knox_enterprise.license.domain.usecase.KnoxLicenseUseCaseImpl
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object LicenseModule {

    @Singleton
    @Provides
    fun provideGetLicenseInfoUseCase(@ApplicationContext context: Context): GetLicenseInfoUseCase {
        return GetLicenseInfoUseCaseImpl(context)
    }

    @Singleton
    @Provides
    fun provideKnoxLicenseUseCase(@ApplicationContext context: Context, errorMapper: KnoxErrorMapper): KnoxLicenseUseCase {
        return KnoxLicenseUseCaseImpl(context, errorMapper)
    }

    @Singleton
    @Provides
    fun provideLicenseRepository(getLicenseInfoUseCase: GetLicenseInfoUseCase, knoxLicenseUseCase: KnoxLicenseUseCase): LicenseRepository {
        return LicenseRepositoryImpl(knoxLicenseUseCase, getLicenseInfoUseCase)
    }
}