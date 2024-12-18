package net.sfelabs.knoxmoduleshowcase

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import net.sfelabs.core.domain.repository.PreferencesRepository
import net.sfelabs.core.knox.android.KnoxContextProvider
import javax.inject.Inject

@HiltAndroidApp
class RootApplication: Application() {
    @Inject
    lateinit var knoxContextProvider: KnoxContextProvider

    override fun onCreate() {
        super.onCreate()
        KnoxContextProvider.init(knoxContextProvider)
        PreferencesRepository.getInstance(this)
    }
}