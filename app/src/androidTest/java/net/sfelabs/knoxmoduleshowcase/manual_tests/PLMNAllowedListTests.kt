package net.sfelabs.knoxmoduleshowcase.manual_tests

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.samsung.android.knox.custom.CustomDeviceManager
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
        private const val TAG = "PLMNAllowedListTests"
        // Bell Canada: MCC 302, MNC 610
        val BELL_CANADA = PlmnId("302610")
        val TEST_PLMN = PlmnId("310260")
    }

    @Test
    fun diagnosePLMNMethods() {
        val systemManager = CustomDeviceManager.getInstance().systemManager
        val systemManagerClass = systemManager::class.java

        Log.i(TAG, "=== SystemManager Class Info ===")
        Log.i(TAG, "Class name: ${systemManagerClass.name}")
        Log.i(TAG, "Superclass: ${systemManagerClass.superclass?.name}")

        Log.i(TAG, "=== Methods containing 'PLMN' ===")

        // Check declared methods (including private)
        systemManagerClass.declaredMethods
            .filter { it.name.contains("PLMN", ignoreCase = true) }
            .forEach { method ->
                val params = method.parameterTypes.joinToString(", ") { it.name }
                Log.i(TAG, "DECLARED: ${method.returnType.name} ${method.name}($params)")
            }

        // Check all public methods (including inherited)
        systemManagerClass.methods
            .filter { it.name.contains("PLMN", ignoreCase = true) }
            .forEach { method ->
                val params = method.parameterTypes.joinToString(", ") { it.name }
                Log.i(TAG, "PUBLIC: ${method.returnType.name} ${method.name}($params)")
            }

        // Also check superclass methods
        var currentClass: Class<*>? = systemManagerClass.superclass
        while (currentClass != null && currentClass != Any::class.java) {
            Log.i(TAG, "=== Checking superclass: ${currentClass.name} ===")
            currentClass.declaredMethods
                .filter { it.name.contains("PLMN", ignoreCase = true) }
                .forEach { method ->
                    val params = method.parameterTypes.joinToString(", ") { it.name }
                    Log.i(TAG, "SUPER: ${method.returnType.name} ${method.name}($params)")
                }
            currentClass = currentClass.superclass
        }

        Log.i(TAG, "=== End of PLMN method search ===")
    }

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
        val result = useCase.invoke(simSlotId = 0)
        assert(result is ApiResult.Success) {
            "Expected Success but got: $result"
        }
        println("PLMN Allowed List: ${(result as ApiResult.Success).data}")
    }
}
