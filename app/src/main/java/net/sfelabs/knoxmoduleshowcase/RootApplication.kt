package net.sfelabs.knoxmoduleshowcase

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.sfelabs.knox.core.android.AndroidApplicationContextProvider
import com.github.jpicklyk.knox.licensing.domain.KnoxLicenseInitializer
import com.github.jpicklyk.knox.licensing.domain.LicenseSelectionStrategy
import com.github.jpicklyk.knox.licensing.domain.LicenseStartupResult
import net.sfelabs.knoxmoduleshowcase.BuildConfig
import javax.inject.Inject

@HiltAndroidApp
class RootApplication: Application() {
    @Inject
    lateinit var applicationContextProvider: AndroidApplicationContextProvider

    @Inject
    lateinit var licenseSelectionStrategy: LicenseSelectionStrategy

    @Inject
    lateinit var knoxLicenseInitializer: KnoxLicenseInitializer

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        AndroidApplicationContextProvider.init(applicationContextProvider)
        // PreferencesRepository and DataStoreSource are provided by Hilt via DataStoreModule
        // and registered with companion objects for backward compatibility

        // Initialize Knox licensing on startup
        initializeKnoxLicensing()
    }

    private fun initializeKnoxLicensing() {
        applicationScope.launch {
            try {
                val result = knoxLicenseInitializer.initialize(
                    context = this@RootApplication,
                    defaultKey = BuildConfig.KNOX_LICENSE_KEY,
                    namedKeysArray = BuildConfig.KNOX_LICENSE_KEYS,
                    licenseSelectionStrategy = licenseSelectionStrategy
                )
                handleLicenseResult(result)
            } catch (e: Exception) {
                Log.e("RootApplication", "Error during Knox license initialization", e)
            }
        }
    }

    private fun handleLicenseResult(result: LicenseStartupResult) {
        when (result) {
            is LicenseStartupResult.AlreadyActivated -> {
                Log.d("RootApplication", "Knox license is already activated")
            }
            is LicenseStartupResult.ActivatedNow -> {
                Log.d("RootApplication", "Knox license successfully activated on startup")
            }
            is LicenseStartupResult.ActivationFailed -> {
                Log.w("RootApplication", "Knox license activation failed: ${result.reason}")
                // Note: Knox features may not work properly
            }
            is LicenseStartupResult.InitializationError -> {
                Log.e("RootApplication", "Knox license initialization error: ${result.reason}")
                // Note: Knox features may not work properly
            }
            is LicenseStartupResult.NotChecked -> {
                Log.w("RootApplication", "Knox license not checked yet")
            }
        }
    }
}