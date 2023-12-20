package net.sfelabs.knox_tactical

import android.os.Bundle
import androidx.test.runner.AndroidJUnitRunner
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress

@SuppressWarnings("unused")
class TacticalJUnitRunner: AndroidJUnitRunner() {
    // androidx.test.internal.runner.RunnerArgs looks for this bundle key
    private val FILTER_BUNDLE_KEY = "filter"

    override fun onCreate(bundle: Bundle) {
        // add the Tactical SDK filter to the test runner's filter list
        bundle.putString(
            FILTER_BUNDLE_KEY,
            TacticalSdkSuppress.Filter::class.java.name
        )
        super.onCreate(bundle)
    }
}