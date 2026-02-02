package com.github.jpicklyk.knox.licensing

import android.content.Context
import com.github.jpicklyk.knox.licensing.data.KnoxErrorMapper
import com.github.jpicklyk.knox.licensing.data.KnoxLicenseHandlerImpl
import com.github.jpicklyk.knox.licensing.data.KnoxLicenseRepository
import com.github.jpicklyk.knox.licensing.data.LicenseKeyProvider
import com.github.jpicklyk.knox.licensing.domain.KnoxLicenseHandler
import com.github.jpicklyk.knox.licensing.domain.LicenseConfiguration
import com.github.jpicklyk.knox.licensing.domain.LicenseSelectionStrategy

object KnoxLicenseFactory {

    /**
     * Creates a KnoxLicenseHandler with license keys from the app's BuildConfig.
     *
     * @param context Application context
     * @param licenseSelectionStrategy Optional strategy for selecting which license key to use
     * @param defaultKey The default license key from app's BuildConfig.KNOX_LICENSE_KEY
     * @param namedKeysArray Array of named keys from app's BuildConfig.KNOX_LICENSE_KEYS
     */
    fun create(
        context: Context,
        licenseSelectionStrategy: LicenseSelectionStrategy?,
        defaultKey: String,
        namedKeysArray: Array<String>?
    ): KnoxLicenseHandler {
        return create(context, LicenseKeyProvider(licenseSelectionStrategy).fromAppBuildConfig(defaultKey, namedKeysArray))
    }

    /**
     * Creates a KnoxLicenseHandler with an explicit license configuration.
     *
     * @param context Application context
     * @param licenseConfiguration The license configuration containing keys
     */
    fun create(context: Context, licenseConfiguration: LicenseConfiguration): KnoxLicenseHandler {
        val knoxErrorMapper = KnoxErrorMapper()
        val knoxLicenseRepository = KnoxLicenseRepository(
            context = context,
            licenseConfiguration = licenseConfiguration,
            knoxErrorMapper = knoxErrorMapper
        )

        return KnoxLicenseHandlerImpl(
            licenseConfiguration = licenseConfiguration,
            knoxLicenseRepository = knoxLicenseRepository
        )
    }

    /**
     * Creates a KnoxLicenseHandler with explicitly provided license keys.
     *
     * @param context Application context
     * @param defaultKey The default/primary license key
     * @param namedKeys Optional map of named license keys for multi-license scenarios
     */
    fun createWithKeys(
        context: Context,
        defaultKey: String,
        namedKeys: Map<String, String> = emptyMap()
    ): KnoxLicenseHandler {
        val licenseConfiguration = LicenseConfiguration(defaultKey, namedKeys)
        return create(context, licenseConfiguration)
    }
}