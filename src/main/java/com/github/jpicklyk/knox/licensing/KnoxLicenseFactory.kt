package com.github.jpicklyk.knox.licensing

import android.content.Context
import com.github.jpicklyk.knox.licensing.data.KnoxErrorMapper
import com.github.jpicklyk.knox.licensing.data.KnoxLicenseHandlerImpl
import com.github.jpicklyk.knox.licensing.data.KnoxLicenseRepository
import com.github.jpicklyk.knox.licensing.data.LicenseKeyProvider
import com.github.jpicklyk.knox.licensing.domain.KnoxLicenseHandler
import com.github.jpicklyk.knox.licensing.domain.LicenseConfiguration

object KnoxLicenseFactory {

    fun create(context: Context): KnoxLicenseHandler {
        return create(context, LicenseKeyProvider().fromBuildConfig())
    }

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

    fun createFromBuildConfig(context: Context): KnoxLicenseHandler {
        return create(context)
    }

    fun createWithKeys(
        context: Context,
        defaultKey: String,
        namedKeys: Map<String, String> = emptyMap()
    ): KnoxLicenseHandler {
        val licenseConfiguration = LicenseConfiguration(defaultKey, namedKeys)
        return create(context, licenseConfiguration)
    }
}