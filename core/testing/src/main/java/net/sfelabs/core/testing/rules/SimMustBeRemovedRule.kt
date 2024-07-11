package net.sfelabs.core.testing.rules

import android.content.Context
import android.telephony.TelephonyManager
import androidx.test.core.app.ApplicationProvider
import org.junit.Assume
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class SimRemoved

class SimMustBeRemovedRule : TestRule {
    override fun apply(statement: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                if (description.getAnnotation(SimRemoved::class.java) != null) {
                    Assume.assumeFalse("SIM card is present, but it should be removed", isSimCardPresent(ApplicationProvider.getApplicationContext()))
                }
                statement.evaluate()
            }
        }
    }

    fun isSimCardPresent(context: Context = ApplicationProvider.getApplicationContext()): Boolean {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        return when (telephonyManager.simState) {
            TelephonyManager.SIM_STATE_READY -> true
            TelephonyManager.SIM_STATE_ABSENT -> false
            TelephonyManager.SIM_STATE_NETWORK_LOCKED -> true
            TelephonyManager.SIM_STATE_PIN_REQUIRED -> true
            TelephonyManager.SIM_STATE_PUK_REQUIRED -> true
            TelephonyManager.SIM_STATE_UNKNOWN -> false
            else -> false
        }
    }
}