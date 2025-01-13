package net.sfelabs.knoxmoduleshowcase.tests.knox_core

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_enterprise.domain.use_cases.AllowOtaUpgradeUseCase
import net.sfelabs.knox_enterprise.domain.use_cases.IsOtaUpgradeAllowedUseCase
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AllowOtaUpgradeTests {

    @Test
    fun allowUpgrade() = runTest {
        val setCase = AllowOtaUpgradeUseCase().invoke(true)
        assert(setCase is ApiResult.Success)
        val getCase = IsOtaUpgradeAllowedUseCase().invoke()
        assert(getCase is ApiResult.Success && getCase.data)
    }

    @Test
    fun disallowUpgrade() = runTest {
        val setCase = AllowOtaUpgradeUseCase().invoke(false)
        assert(setCase is ApiResult.Success)
        val getCase = IsOtaUpgradeAllowedUseCase().invoke()
        assert(getCase is ApiResult.Success && !getCase.data)
    }

    @After
    fun resetRestriction() {
        allowUpgrade()
    }
}