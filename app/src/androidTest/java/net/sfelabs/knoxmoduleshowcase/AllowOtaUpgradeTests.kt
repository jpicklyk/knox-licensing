package net.sfelabs.knoxmoduleshowcase

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import net.sfelabs.common.core.ApiCall
import net.sfelabs.knox_common.di.KnoxModule
import net.sfelabs.knox_common.domain.use_cases.AllowOtaUpgradeUseCase
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
        val useCase = AllowOtaUpgradeUseCase(edm)
        val result = useCase.invoke(true)
        assert(result is ApiCall.Success)
    }
}