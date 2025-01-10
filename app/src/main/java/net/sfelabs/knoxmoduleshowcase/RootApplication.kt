package net.sfelabs.knoxmoduleshowcase

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import net.sfelabs.core.domain.repository.PreferencesRepository
import net.sfelabs.core.knox.android.AndroidApplicationContextProvider
import javax.inject.Inject

@HiltAndroidApp
class RootApplication: Application() {
    @Inject
    lateinit var applicationContextProvider: AndroidApplicationContextProvider

    override fun onCreate() {
        super.onCreate()
        AndroidApplicationContextProvider.init(applicationContextProvider)
        PreferencesRepository.getInstance(this)
    }
}