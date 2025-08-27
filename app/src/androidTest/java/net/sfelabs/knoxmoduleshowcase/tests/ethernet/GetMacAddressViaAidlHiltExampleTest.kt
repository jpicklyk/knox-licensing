package net.sfelabs.knoxmoduleshowcase.tests.ethernet

import android.content.Context
import androidx.test.filters.LargeTest
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.EthernetAidlUseCaseFactory
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.GetMacAddressViaAidlUseCase
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Example test showing how the AIDL UseCase CAN be used with Hilt if desired.
 * This demonstrates that while the UseCase is DI-framework agnostic, 
 * it can still be integrated with Hilt using the factory pattern.
 */
@HiltAndroidTest
@LargeTest
class GetMacAddressViaAidlHiltExampleTest {
    
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    
    @Inject
    lateinit var getMacAddressViaAidlUseCase: GetMacAddressViaAidlUseCase
    
    @Before
    fun setUp() {
        hiltRule.inject()
    }
    
    @Test
    fun testHiltInjectedUseCaseWorks() = runBlocking {
        assertNotNull("UseCase should be injected via Hilt", getMacAddressViaAidlUseCase)
        
        val result = getMacAddressViaAidlUseCase.invoke("eth0")
        assertNotNull("Result should not be null", result)
        
        when (result) {
            is ApiResult.Success -> {
                println("Hilt-injected UseCase successfully retrieved MAC: ${result.data}")
            }
            is ApiResult.Error -> {
                println("Expected error from Hilt-injected UseCase: ${result.apiError.message}")
            }
            is ApiResult.NotSupported -> {
                println("AIDL service not supported (Hilt injection)")
            }
        }
    }
    
    /**
     * Example Hilt module showing how to integrate the factory-based UseCase with Hilt.
     * This is optional - the UseCase works fine without any DI framework.
     */
    @Module
    @InstallIn(SingletonComponent::class)
    object TestEthernetAidlModule {
        
        @Provides
        @Singleton
        fun provideGetMacAddressViaAidlUseCase(
            @ApplicationContext context: Context
        ): GetMacAddressViaAidlUseCase {
            return EthernetAidlUseCaseFactory.createGetMacAddressUseCase(context)
        }
    }
}