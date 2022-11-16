package net.sfelabs.knox_common

import net.sfelabs.knox_tactical.domain.use_cases.KnoxFunctionProbeUseCase
import org.junit.Test

class KnoxFunctionProbeUseCaseTest {
    private val useCase = net.sfelabs.knox_tactical.domain.use_cases.KnoxFunctionProbeUseCase()
    @Test
    fun testTacticalKnoxSdkCall() {
        val className = "com.samsung.android.knox.restriction.RestrictionPolicy"
        val functionName = "isTacticalDeviceModeEnabled"

        val result = useCase.invoke(className, functionName)
        assert(result)
    }

    @Test
    fun testStandardKnoxSdkCall() {
        val className = "com.samsung.android.knox.restriction.RestrictionPolicy"
        val functionName = "allowPowerOff"

        val result = useCase.invoke(className, functionName)
        assert(result)
    }

    @Test
    fun testStandardKnoxSdkBadSpellingCall() {
        val className = "com.samsung.android.knox.restriction.RestrictionPolicy"
        val functionName = "allowPowerOfff"

        val result = useCase.invoke(className, functionName)
        assert(!result)
    }

    @Test
    fun testStandardKnoxSdkCaseSensativityCall() {
        val className = "com.samsung.android.knox.restriction.RestrictionPolicy"
        val functionName = "allowpoweroff"

        val result = useCase.invoke(className, functionName)
        assert(result)
    }

    @Test
    fun testApiDoesNotExistCall() {
        val className = "com.samsung.android.knox.restriction.RestrictionPolicy"
        val functionName = "iDoNotExist"

        val result = useCase.invoke(className, functionName)
        assert(!result)
    }
}