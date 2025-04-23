package net.sfelabs.knoxmoduleshowcase

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import net.sfelabs.knox.core.android.AndroidApplicationContextProvider
import net.sfelabs.knox.core.common.domain.repository.PreferencesRepository
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