import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.model.DefaultApiError
import net.sfelabs.core.domain.usecase.executor.UseCaseBuilder
import net.sfelabs.core.domain.usecase.executor.UseCaseBuilder.UseCaseBuilderState
import net.sfelabs.core.domain.usecase.MainCoroutineRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class UseCaseBuilderTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var builder: UseCaseBuilder

    @Before
    fun setup() {
        builder = UseCaseBuilder()
    }

    @Test
    fun `sequential execution succeeds when all operations succeed`() = runTest {
        // Given
        val results = mutableListOf<Int>()

        // When
        val apiResults = builder.sequential {
            results.add(1)
            ApiResult.Success(1)
        }
            .add {
                results.add(2)
                ApiResult.Success(2)
            }
            .execute()

        // Then
        assertEquals(2, apiResults.size)
        assertTrue(apiResults.all { it is ApiResult.Success })
        assertEquals(listOf(1, 2), results)
    }

    @Test
    fun `sequential execution stops on first failure`() = runTest {
        // Given
        val results = mutableListOf<Int>()

        // When
        val apiResults = builder.sequential {
            results.add(1)
            ApiResult.Success(1)
        }
            .add {
                results.add(2)
                ApiResult.Error(DefaultApiError.UnexpectedError())
            }
            .add {
                results.add(3)
                ApiResult.Success(3)
            }
            .execute()

        // Then
        assertEquals(2, apiResults.size)
        assertEquals(listOf(1, 2), results)
        assertTrue(apiResults[0] is ApiResult.Success)
        assertTrue(apiResults[1] is ApiResult.Error)
    }

    @Test
    fun `parallel execution executes all operations regardless of failures`() = runTest {
        // Given
        val results = mutableListOf<Int>()

        // When
        val apiResults = builder.parallel {
            results.add(1)
            ApiResult.Success(1)
        }
            .add {
                results.add(2)
                ApiResult.Error(DefaultApiError.UnexpectedError())
            }
            .add {
                results.add(3)
                ApiResult.Success(3)
            }
            .execute()

        // Then
        assertEquals(3, apiResults.size)
        assertTrue(results.containsAll(listOf(1, 2, 3)))
    }

    @Test
    fun `any execution stops on first success`() = runTest {
        // Given
        val results = mutableListOf<Int>()

        // When
        val apiResults = builder.any {
            results.add(1)
            ApiResult.Error(DefaultApiError.UnexpectedError())
        }
            .add {
                results.add(2)
                ApiResult.Success(2)
            }
            .add {
                results.add(3)
                ApiResult.Success(3)
            }
            .execute()

        // Then
        assertEquals(2, apiResults.size)
        assertEquals(listOf(1, 2), results)
        assertTrue(apiResults.last() is ApiResult.Success)
    }

    @Test
    fun `when predicate prevents single operation execution`() = runTest {
        // Given
        val results = mutableListOf<Int>()

        // When
        val apiResults = builder.sequential {
            results.add(1)
            ApiResult.Success(1)
        }
            .add {
                results.add(2)
                ApiResult.Success(2)
            }
            .`when` { false }  // This affects the operation that added 2
            .execute()

        // Then
        assertEquals(2, apiResults.size)
        assertEquals(listOf(1), results)  // Only 1 should be added, 2 was skipped
    }

    @Test
    fun `when predicate applies to immediate previous operation only`() = runTest {
        // Given
        val results = mutableListOf<Int>()

        // When
        val apiResults = builder.sequential {
            results.add(1)
            ApiResult.Success(1)
        }
            .add {
                results.add(2)
                ApiResult.Success(2)
            }
            .`when` { false }  // This affects only operation 2
            .add {
                results.add(3)
                ApiResult.Success(3)
            }
            .execute()

        // Then
        assertEquals(3, apiResults.size)
        assertEquals(listOf(1, 3), results)  // 2 was skipped, but 3 still executes
    }

    @Test
    fun `when predicate prevents operation execution`() = runTest {
        // Given
        val results = mutableListOf<Int>()

        // When
        val apiResults = builder.sequential {
            results.add(1)
            ApiResult.Success(1)
        }
            .add {
                results.add(2)
                ApiResult.Success(2)
            }
            .add {
                ApiResult.Success(3)
            }
            .`when` { false }
            .execute()

        // Then
        assertEquals(3, apiResults.size)
        assertTrue(apiResults[2] is ApiResult.Success)
        assertEquals(Unit, (apiResults[2] as ApiResult.Success).data)  // Predicate false returns Success(Unit)
        assertEquals(listOf(1, 2), results)
    }

    @Test
    fun `retry policy is applied correctly`() = runTest {
        // Given
        var attempts = 0

        // When
        val apiResults = builder.sequential {
            attempts++
            if (attempts < 3) {
                ApiResult.Error(DefaultApiError.UnexpectedError())
            } else {
                ApiResult.Success(attempts)
            }
        }
            .withRetry(maxAttempts = 3)
            .execute()

        // Then
        assertEquals(1, apiResults.size)
        assertTrue(apiResults[0] is ApiResult.Success)
        assertEquals(3, attempts)
    }

    @Test
    fun `fallback is executed on failure`() = runTest {
        // Given
        val results = mutableListOf<String>()

        // When
        val apiResults = builder.sequential {
            results.add("main")
            ApiResult.Error(DefaultApiError.UnexpectedError())
        }
            .withFallback {
                results.add("fallback")
                ApiResult.Success("fallback")
            }
            .execute()

        // Then
        assertEquals(1, apiResults.size)
        assertEquals(listOf("main", "fallback"), results)
        assertTrue(apiResults[0] is ApiResult.Success)
    }

    @Test
    fun `state tracking reports operation progress`() = runTest {
        // Given
        val states = mutableListOf<UseCaseBuilderState>()

        // When
        val apiResults = builder.sequential {
            ApiResult.Success(1)
        }
            .onStateChanged { state ->
                states.add(state)
            }
            .add {
                ApiResult.Success(2)
            }
            .execute()

        // Then
        assertTrue(apiResults.size == 2)
        assertTrue(states.isNotEmpty())
        assertTrue(states.last().executedOperations.size == 2)
        assertTrue(states.last().executedOperations.all { it.wasSuccessful })
    }

    @Test
    fun `state tracking captures skipped operations`() = runTest {
        // Given
        val states = mutableListOf<UseCaseBuilderState>()

        // When
        builder.sequential { ApiResult.Success(1) }
            .onStateChanged { state -> states.add(state) }
            .add { ApiResult.Success(2) }
            .`when` { false }
            .execute()

        // Then
        assertTrue(states.isNotEmpty())
        assertTrue(states.last().executedOperations.any { it.skipped })
    }

    @Test
    fun `state tracking captures failed operations`() = runTest {
        // Given
        val states = mutableListOf<UseCaseBuilderState>()

        // When
        builder.sequential { ApiResult.Success(1) }
            .onStateChanged { state -> states.add(state) }
            .add { ApiResult.Error(DefaultApiError.UnexpectedError()) }
            .execute()

        // Then
        assertTrue(states.isNotEmpty())
        assertTrue(states.last().executedOperations.any { !it.wasSuccessful })
    }

    @Test
    fun `state tracking captures fallback operations`() = runTest {
        // Given
        val states = mutableListOf<UseCaseBuilderState>()

        // When
        builder.sequential { ApiResult.Success(1) }
            .onStateChanged { state -> states.add(state) }
            .add { ApiResult.Error(DefaultApiError.UnexpectedError()) }
            .withFallback { ApiResult.Success(2) }
            .execute()

        // Then
        assertTrue(states.isNotEmpty())
        // Should see both the failed operation and the successful fallback
        assertTrue(states.last().executedOperations.size == 3)
    }

    @Test
    fun `state tracking in parallel execution`() = runTest {
        // Given
        val states = mutableListOf<UseCaseBuilderState>()
        var executionCount = 0

        // When
        builder.parallel {
            executionCount++
            ApiResult.Success(1)
        }
            .onStateChanged { state ->
                println("State changed: operations=${state.executedOperations.size}")
                states.add(state)
            }
            .add {
                executionCount++
                ApiResult.Success(2)
            }
            .execute()

        // Then
        println("Execution count: $executionCount")
        println("States size: ${states.size}")
        println("Last state operations: ${states.lastOrNull()?.executedOperations?.size}")

        assertEquals(2, executionCount)
        assertTrue("Should have received state updates", states.isNotEmpty())
        assertEquals("Should have tracked both operations",
            2, states.last().executedOperations.size)
        assertTrue("All operations should be successful",
            states.last().executedOperations.all { it.wasSuccessful })
    }

    @Test
    fun `timeout cancels execution`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)

        // Given
        val results = mutableListOf<Int>()

        // When
        builder.sequential {
            results.add(1)
            delay(100)
            ApiResult.Success(1)
        }
            .withTimeout(50.milliseconds)
            .withScope(CoroutineScope(testDispatcher))
            .execute()
            .also { apiResults ->
                advanceTimeBy(51)
                println("Results size: ${results.size}")
                println("API Results: $apiResults")
                assertTrue(apiResults.isEmpty())
                assertEquals(1, results.size)
            }
    }

    @Test
    fun `handles exceptions during execution`() = runTest {
        // When
        val apiResults = builder.sequential {
            throw IllegalStateException("Test exception")
            ApiResult.Success(1)
        }.execute()

        // Then
        assertEquals(1, apiResults.size)
        assertTrue(apiResults[0] is ApiResult.Error)
    }

    @Test
    fun `executes mixed operation chain`() = runTest {
        // Given
        val results = mutableListOf<Int>()

        // When
        val apiResults = builder.sequential {
            results.add(1)
            ApiResult.Success(1)
        }
            .then()
            .parallel()
            .add {
                results.add(2)
                ApiResult.Success(2)
            }
            .add {
                results.add(3)
                ApiResult.Success(3)
            }
            .then()
            .add {
                results.add(4)
                ApiResult.Success(4)
            }
            .execute()

        // Then
        assertEquals(4, apiResults.size)
        assertTrue(apiResults.all { it is ApiResult.Success })
    }

    @Test
    fun `handles retry with fallback`() = runTest {
        var attempts = 0

        val apiResults = builder.sequential {
            attempts++
            ApiResult.Error(DefaultApiError.UnexpectedError())
        }
            .withRetry(maxAttempts = 2)
            .withFallback {
                ApiResult.Success("fallback")
            }
            .execute()

        assertEquals(1, apiResults.size)
        assertEquals(2, attempts)
        assertTrue(apiResults[0] is ApiResult.Success)
    }

    @Test
    fun `handles not supported results`() = runTest {
        val apiResults = builder.sequential {
            ApiResult.NotSupported
        }
            .add {
                ApiResult.Success(1)  // Should not execute
            }
            .execute()

        assertEquals(1, apiResults.size)
        assertTrue(apiResults[0] is ApiResult.NotSupported)
    }
}