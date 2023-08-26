package net.sfelabs.knoxmoduleshowcase

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.knox_common.di.KnoxModule
import net.sfelabs.knox_common.domain.use_cases.AllowOtaUpgradeUseCase
import net.sfelabs.knox_common.domain.use_cases.IsOtaUpgradeAllowedUseCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AllowOtaUpgradeTests {
    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().context
    }

    @Test
    fun allowUpgrade() = runTest {
        val edm = KnoxModule.provideKnoxEnterpriseDeviceManager(context)
        val setCase = AllowOtaUpgradeUseCase(edm).invoke(true)
        assert(setCase is ApiCall.Success)
        val getCase = IsOtaUpgradeAllowedUseCase(edm).invoke()
        assert(getCase is ApiCall.Success && getCase.data)
    }

    @Test
    fun disallowUpgrade() = runTest {
        val edm = KnoxModule.provideKnoxEnterpriseDeviceManager(context)
        val setCase = AllowOtaUpgradeUseCase(edm).invoke(false)
        assert(setCase is ApiCall.Success)
        val getCase = IsOtaUpgradeAllowedUseCase(edm).invoke()
        assert(getCase is ApiCall.Success && !getCase.data)
    }

    @After
    fun resetRestriction() {
        allowUpgrade()
    }
}