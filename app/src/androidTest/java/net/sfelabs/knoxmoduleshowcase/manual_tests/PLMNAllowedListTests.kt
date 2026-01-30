package net.sfelabs.knoxmoduleshowcase.manual_tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox.core.testing.rules.CarrierDataRequired
import net.sfelabs.knox_tactical.data.dto.PlmnId
import net.sfelabs.knox_tactical.domain.use_cases.radio.AddPLMNAllowedListUseCase
import net.sfelabs.knox_tactical.domain.use_cases.radio.GetPLMNAllowedListUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@CarrierDataRequired
class PLMNAllowedListTests {

    companion object {
        // Bell Canada: MCC 302, MNC 610
        val BELL_CANADA = PlmnId("302610")
        val TEST_PLMN = PlmnId("310260")
    }

    // diagnosePLMNMethods() removed - was a debugging test that required direct SDK access

    @Test
    fun addBellCanadaToPLMNAllowedList() = runTest {
        val useCase = AddPLMNAllowedListUseCase()
        val result = useCase.invoke(listOf(BELL_CANADA), simSlotId = 0)
        assert(result is ApiResult.Success) {
            "Expected Success but got: $result"
        }
    }

    @Test
    fun addRandomToPLMNAllowedList() = runTest {
        val useCase = AddPLMNAllowedListUseCase()
        val result = useCase.invoke(listOf(TEST_PLMN), simSlotId = 0)
        assert(result is ApiResult.Success) {
            "Expected Success but got: $result"
        }
    }

    @Test
    fun getPLMNAllowedList() = runTest {
        val useCase = GetPLMNAllowedListUseCase()
        val result = useCase.invoke(0)
        assert(result is ApiResult.Success) {
            "Expected Success but got: $result"
        }
        println("PLMN Allowed List: ${(result as ApiResult.Success).data}")
    }
}
