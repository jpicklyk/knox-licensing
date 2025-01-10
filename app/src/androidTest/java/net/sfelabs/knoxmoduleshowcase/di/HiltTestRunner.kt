package net.sfelabs.knoxmoduleshowcase.di

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication
import net.sfelabs.core.knox.android.AndroidApplicationContextProvider
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress

@Suppress("unused")
class HiltTestRunner : AndroidJUnitRunner() {
    override fun onCreate(bundle: Bundle) {
        // add the Tactical SDK filter to the test runner's filter list
        bundle.putString(
            "filter",
            TacticalSdkSuppress.Filter::class.java.name
        )

        super.onCreate(bundle)
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val testProvider = object : AndroidApplicationContextProvider {
            override fun getContext(): Context {
                return appContext
            }
        }
        AndroidApplicationContextProvider.init(testProvider)
    }
    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}