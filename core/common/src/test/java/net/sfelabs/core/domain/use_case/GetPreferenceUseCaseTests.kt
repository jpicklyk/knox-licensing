package net.sfelabs.core.domain.use_case

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import net.sfelabs.core.domain.repository.PreferencesRepository
import org.junit.Assert.assertEquals
import org.junit.Test

class GetPreferenceUseCaseTests {
    private val repository: PreferencesRepository = mockk()

    @Test
    fun `invoke returns correct boolean from repository`() = runBlocking {
        val useCase = GetPreferenceUseCase<Boolean>(repository)
        val key = "test_boolean_key"
        val expectedValue = true

        every { repository.getValue(key, false) } returns flowOf(expectedValue)

        val result = useCase(key, false).first()

        assertEquals(expectedValue, result)
    }

    @Test
    fun `invoke returns correct string from repository`() = runBlocking {
        val useCase = GetPreferenceUseCase<String>(repository)
        val key = "test_string_key"
        val expectedValue = "test_value"

        every { repository.getValue(key, "") } returns flowOf(expectedValue)

        val result = useCase(key, "").first()

        assertEquals(expectedValue, result)
    }

    @Test
    fun `invoke returns correct int from repository`() = runBlocking {
        val useCase = GetPreferenceUseCase<Int>(repository)
        val key = "test_int_key"
        val expectedValue = 42

        every { repository.getValue(key, 0) } returns flowOf(expectedValue)

        val result = useCase(key, 0).first()

        assertEquals(expectedValue, result)
    }

    @Test
    fun `invoke returns correct float from repository`() = runBlocking {
        val useCase = GetPreferenceUseCase<Float>(repository)
        val key = "test_float_key"
        val expectedValue = 3.14f

        every { repository.getValue(key, 0f) } returns flowOf(expectedValue)

        val result = useCase(key, 0f).first()

        assertEquals(expectedValue, result)
    }

    @Test
    fun `invoke returns correct long from repository`() = runBlocking {
        val useCase = GetPreferenceUseCase<Long>(repository)
        val key = "test_long_key"
        val expectedValue = 1234567890L

        every { repository.getValue(key, 0L) } returns flowOf(expectedValue)

        val result = useCase(key, 0L).first()

        assertEquals(expectedValue, result)
    }

    @Test
    fun `invoke returns correct set of strings from repository`() = runBlocking {
        val useCase = GetPreferenceUseCase<Set<String>>(repository)
        val key = "test_set_key"
        val expectedValue = setOf("value1", "value2", "value3")

        every { repository.getValue(key, emptySet<String>()) } returns flowOf(expectedValue)

        val result = useCase(key, emptySet()).first()

        assertEquals(expectedValue, result)
    }
}