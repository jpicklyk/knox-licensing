package net.sfelabs.knox_enterprise.license.factory

import android.content.Context
import net.sfelabs.knox_enterprise.license.data.KnoxErrorMapper
import net.sfelabs.knox_enterprise.license.data.LicenseRepositoryImpl
import net.sfelabs.knox_enterprise.license.domain.repository.LicenseRepository
import net.sfelabs.knox_enterprise.license.domain.usecase.GetLicenseInfoUseCase
import net.sfelabs.knox_enterprise.license.domain.usecase.GetLicenseInfoUseCaseImpl
import net.sfelabs.knox_enterprise.license.domain.usecase.KnoxLicenseUseCase
import net.sfelabs.knox_enterprise.license.domain.usecase.KnoxLicenseUseCaseImpl

/**
 * Factory for creating license-related use cases and repositories.
 * This allows DI frameworks to create implementations without
 * needing access to internal implementation classes.
 */
interface LicenseFactory {
    fun createGetLicenseInfoUseCase(context: Context): GetLicenseInfoUseCase
    fun createKnoxLicenseUseCase(context: Context, errorMapper: KnoxErrorMapper): KnoxLicenseUseCase
    fun createLicenseRepository(
        knoxLicenseUseCase: KnoxLicenseUseCase,
        getLicenseInfoUseCase: GetLicenseInfoUseCase
    ): LicenseRepository
}

/**
 * Default factory implementation.
 */
object DefaultLicenseFactory : LicenseFactory {
    override fun createGetLicenseInfoUseCase(context: Context): GetLicenseInfoUseCase {
        return GetLicenseInfoUseCaseImpl(context)
    }

    override fun createKnoxLicenseUseCase(context: Context, errorMapper: KnoxErrorMapper): KnoxLicenseUseCase {
        return KnoxLicenseUseCaseImpl(context, errorMapper)
    }

    override fun createLicenseRepository(
        knoxLicenseUseCase: KnoxLicenseUseCase,
        getLicenseInfoUseCase: GetLicenseInfoUseCase
    ): LicenseRepository {
        return LicenseRepositoryImpl(knoxLicenseUseCase, getLicenseInfoUseCase)
    }
}
