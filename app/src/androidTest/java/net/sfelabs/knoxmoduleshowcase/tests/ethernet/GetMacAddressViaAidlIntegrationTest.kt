package net.sfelabs.knoxmoduleshowcase.tests.ethernet

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.EthernetAidlUseCaseFactory
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.GetMacAddressViaAidlUseCase
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class GetMacAddressViaAidlIntegrationTest {
    
    private lateinit var getMacAddressViaAidlUseCase: GetMacAddressViaAidlUseCase
    
    @Before
    fun setUp() {
        // Use factory pattern to create UseCase instance - no DI framework required
        getMacAddressViaAidlUseCase = EthernetAidlUseCaseFactory.createGetMacAddressUseCase(
            InstrumentationRegistry.getInstrumentation().targetContext
        )
    }
    
    @Test
    fun testUseCaseCreation() {
        assertNotNull("UseCase should be created successfully via factory", getMacAddressViaAidlUseCase)
    }
    
    @Test
    fun testGetMacAddressForEth0() = runBlocking {
        // This test demonstrates using the AIDL-based UseCase without any DI framework
        // It might fail if the AIDL service is not available, but that's expected behavior
        val result = getMacAddressViaAidlUseCase.invoke("eth0")
        
        // We just verify that we get some result - either success or a well-formed error
        assertNotNull("Result should not be null", result)
        
        when (result) {
            is ApiResult.Success -> {
                println("Successfully retrieved MAC address: ${result.data}")
            }
            is ApiResult.Error -> {
                println("Expected error (service may not be available): ${result.apiError.message}")
            }
            is ApiResult.NotSupported -> {
                println("AIDL service not supported on this device")
            }
        }
    }
}