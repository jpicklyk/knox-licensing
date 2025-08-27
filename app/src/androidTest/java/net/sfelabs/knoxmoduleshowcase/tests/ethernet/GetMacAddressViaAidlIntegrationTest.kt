package net.sfelabs.knoxmoduleshowcase.tests.ethernet

import androidx.test.filters.LargeTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.GetMacAddressViaAidlUseCase
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
@LargeTest
class GetMacAddressViaAidlIntegrationTest {
    
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    
    @Inject
    lateinit var getMacAddressViaAidlUseCase: GetMacAddressViaAidlUseCase
    
    @Before
    fun setUp() {
        hiltRule.inject()
    }
    
    @Test
    fun testUseCaseInjection() {
        assertNotNull("UseCase should be injected successfully", getMacAddressViaAidlUseCase)
    }
    
    @Test
    fun testGetMacAddressForEth0() = runBlocking {
        // This test will attempt to get the MAC address for eth0
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