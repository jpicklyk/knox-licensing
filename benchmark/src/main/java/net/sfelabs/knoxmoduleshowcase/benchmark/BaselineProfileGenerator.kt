package net.sfelabs.knoxmoduleshowcase.benchmark

import android.content.Intent
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Generates baseline profiles for the Knox Module Showcase app.
 * 
 * This test will capture method traces during typical user journeys
 * and generate baseline profiles that improve app startup performance.
 * 
 * Run this test to generate baseline profiles:
 * ./gradlew :benchmark:connectedBenchmarkAndroidTest
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

    @get:Rule
    val baselineProfileRule = BaselineProfileRule()
    
    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @Test
    fun generate() = baselineProfileRule.collect(
        packageName = "net.sfelabs.knoxmoduleshowcase"
    ) {
        try {
            // Start the app and wait for it to be fully displayed
            startActivityAndWait()
        } catch (e: Exception) {
            // If startActivityAndWait fails, try manual launch
            device.pressHome()
            Thread.sleep(1000)
            
            // Launch app via intent
            val context = InstrumentationRegistry.getInstrumentation().context
            val intent = context.packageManager.getLaunchIntentForPackage("net.sfelabs.knoxmoduleshowcase")
            intent?.let {
                it.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(it)
            }
        }
        
        // Wait for the app to settle and complete initial loading
        Thread.sleep(5000)
        
        // Basic interaction to capture UI loading patterns
        device.waitForIdle()
        
        // The baseline profile will capture the startup code paths
        // and any immediate UI rendering that happens
    }
}