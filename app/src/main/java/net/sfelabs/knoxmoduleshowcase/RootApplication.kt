package net.sfelabs.knoxmoduleshowcase

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.sfelabs.knox.core.android.AndroidApplicationContextProvider
import net.sfelabs.knox.core.common.domain.repository.PreferencesRepository
import com.github.jpicklyk.knox.licensing.domain.KnoxStartupManager
import com.github.jpicklyk.knox.licensing.domain.LicenseSelectionStrategy
import com.github.jpicklyk.knox.licensing.domain.LicenseStartupResult
import javax.inject.Inject

@HiltAndroidApp
class RootApplication: Application() {
    @Inject
    lateinit var applicationContextProvider: AndroidApplicationContextProvider

    @Inject
    lateinit var licenseSelectionStrategy: LicenseSelectionStrategy

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        AndroidApplicationContextProvider.init(applicationContextProvider)
        PreferencesRepository.getInstance(this)

        // Initialize Knox licensing on startup
        initializeKnoxLicensing()
    }

    private fun initializeKnoxLicensing() {
        applicationScope.launch {
            try {
                val result = KnoxStartupManager.initializeKnoxLicensing(
                    this@RootApplication,
                    licenseSelectionStrategy
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