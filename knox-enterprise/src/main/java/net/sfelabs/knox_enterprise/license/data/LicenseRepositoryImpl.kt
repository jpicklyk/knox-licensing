package net.sfelabs.knox_enterprise.license.data

import android.util.Log
import net.sfelabs.knox_enterprise.license.domain.repository.LicenseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.sfelabs.knox_enterprise.license.domain.usecase.GetLicenseInfoUseCase
import net.sfelabs.knox_enterprise.license.domain.usecase.KnoxLicenseUseCase
import net.sfelabs.knox_enterprise.license.presentation.LicenseState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Deprecated("Use knox-licensing module instead", ReplaceWith("KnoxLicenseFactory.createFromBuildConfig(context)", "com.github.jpicklyk.knox.licensing.KnoxLicenseFactory"))
internal class LicenseRepositoryImpl @Inject constructor(
    private val knoxLicenseUseCase: KnoxLicenseUseCase,
    private val getLicenseInfoUseCase: GetLicenseInfoUseCase
) : LicenseRepository {
    private val tag = "KnoxLicenseRepository"

    // Placeholder license key since this module no longer has BuildConfig.KNOX_LICENSE_KEY
    // This code should be replaced with knox-licensing module
    private val placeholderLicenseKey = "PLACEHOLDER_LICENSE_KEY"

    private val _licenseState = MutableStateFlow<LicenseState>(LicenseState.Loading)
    override val licenseState: StateFlow<LicenseState> = _licenseState.asStateFlow()

    init {
        // Launch a coroutine to fetch the initial license state
        CoroutineScope(Dispatchers.IO).launch {
            refreshLicenseState()
        }
    }

    override suspend fun refreshLicenseState() {
        Log.d(tag, "Refreshing license state")
        _licenseState.value = getLicenseInfoUseCase()
    }

    override suspend fun activateLicense() {
        Log.w(tag, "Using deprecated license activation - consider migrating to knox-licensing module")
        _licenseState.value = knoxLicenseUseCase(licenseKey = placeholderLicenseKey)
        refreshLicenseState() // Refresh the state after activation
    }

    override suspend fun deactivateLicense() {
        Log.w(tag, "Using deprecated license deactivation - consider migrating to knox-licensing module")
        _licenseState.value = knoxLicenseUseCase(activate = false, licenseKey = placeholderLicenseKey)
        refreshLicenseState() // Refresh the state after deactivation
    }
}