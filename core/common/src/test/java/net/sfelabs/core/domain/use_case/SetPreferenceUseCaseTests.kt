package net.sfelabs.core.domain.use_case

import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import net.sfelabs.core.domain.repository.PreferencesRepository
import org.junit.Test

class SetPreferenceUseCaseTests {
    private val repository: PreferencesRepository = mockk(relaxed = true)

    @Test
    fun `invoke calls repository setValue with correct boolean parameter`() = runBlocking {
        val useCase = SetPreferenceUseCase<Boolean>(repository)
        val key = "test_boolean_key"
        val value = true

        useCase(key, value)

        coVerify { repository.setValue(key, value) }
    }

    @Test
    fun `invoke calls repository setValue with correct string parameter`() = runBlocking {
        val useCase = SetPreferenceUseCase<String>(repository)
        val key = "test_string_key"
        val value = "test_value"

        useCase(key, value)

        coVerify { repository.setValue(key, value) }
    }

    @Test
    fun `invoke calls repository setValue with correct int parameter`() = runBlocking {
        val useCase = SetPreferenceUseCase<Int>(repository)
        val key = "test_int_key"
        val value = 42

        useCase(key, value)

        coVerify { repository.setValue(key, value) }
    }

    @Test
    fun `invoke calls repository setValue with correct float parameter`() = runBlocking {
        val useCase = SetPreferenceUseCase<Float>(repository)
        val key = "test_float_key"
        val value = 3.14f

        useCase(key, value)

        coVerify { repository.setValue(key, value) }
    }

    @Test
    fun `invoke calls repository setValue with correct long parameter`() = runBlocking {
        val useCase = SetPreferenceUseCase<Long>(repository)
        val key = "test_long_key"
        val value = 1234567890L

        useCase(key, value)

        coVerify { repository.setValue(key, value) }
    }

    @Test
    fun `invoke calls repository setValue with correct set of strings parameter`() = runBlocking {
        val useCase = SetPreferenceUseCase<Set<String>>(repository)
        val key = "test_set_key"
        val value = setOf("value1", "value2", "value3")

        useCase(key, value)

        coVerify { repository.setValue(key, value) }
    }
}