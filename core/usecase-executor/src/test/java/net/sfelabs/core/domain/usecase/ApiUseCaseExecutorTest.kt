package net.sfelabs.core.domain.usecase

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.executor.UseCaseExecutor
import net.sfelabs.core.domain.usecase.model.DefaultApiError
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class ApiUseCaseExecutorTest {
    private lateinit var executor: UseCaseExecutor

    @Before
    fun setup() {
        executor = UseCaseExecutor()
    }

    @Test
    fun `executeWithRetry retries based on predicate`() = runTest {
        var attempts = 0
        val operation = suspend {
            attempts++
            if (attempts < 3) ApiResult.Error(DefaultApiError.UnexpectedError("Retry"))
            else ApiResult.Success("Success")
        }

        val result = executor.executeWithRetry(
            operation = operation,
            type = String::class.java,
            maxAttempts = 3,
            predicate = { it.apiError.message == "Retry" }
        )

        assertEquals(3, attempts)
        assertTrue(result is ApiResult.Success)
        assertEquals("Success", (result as ApiResult.Success).data)
    }

    @Test
    fun `executeWithFallback uses fallback when primary fails`() = runTest {
        val primaryOp = suspend { ApiResult.Error(DefaultApiError.UnexpectedError()) }
        val fallbackOp = suspend { ApiResult.Success("Fallback") }

        val result = executor.executeWithFallback(primaryOp, fallbackOp, String::class.java)

        assertTrue(result is ApiResult.Success)
        assertEquals("Fallback", (result as ApiResult.Success).data)
    }

    @Test
    fun `executeAndMap transforms success results`() = runTest {
        val result = executor.executeAndMap(
            operation = { ApiResult.Success(42) },
            sourceType = Int::class.java,
            targetType = String::class.java,
            mapper = { it.toString() }
        )

        assertTrue(result is ApiResult.Success)
        assertEquals("42", (result as ApiResult.Success).data)
    }

    @Test
    fun `executeWithErrorHandler handles errors`() = runTest {
        var errorHandled = false
        val result = executor.executeWithErrorHandler(
            operation = { ApiResult.Error(DefaultApiError.UnexpectedError()) },
            type = String::class.java
        ) { errorHandled = true }

        assertNull(result)
        assertTrue(errorHandled)
    }

    @Test
    fun `executeIf respects condition`() = runTest {
        val trueResult = executor.executeIf(
            condition = true,
            operation = { ApiResult.Success("Executed") },
            type = String::class.java
        )
        val falseResult = executor.executeIf(
            condition = false,
            operation = { ApiResult.Success("Not Executed") },
            type = String::class.java
        )

        assertTrue(trueResult is ApiResult.Success)
        assertEquals("Executed", (trueResult as ApiResult.Success).data)
        assertNull(falseResult)
    }

    @Test
    fun `executeAndMap preserves error states`() = runTest {
        val error = ApiResult.Error(DefaultApiError.UnexpectedError())
        val result = executor.executeAndMap(
            operation = { error },
            sourceType = Int::class.java,
            targetType = String::class.java
        ) { it.toString() }

        assertTrue(result is ApiResult.Error)
    }

    @Test
    fun `executeWithRetry stops on non-retryable error`() = runTest {
        var attempts = 0
        val operation = suspend {
            attempts++
            ApiResult.Error(DefaultApiError.UnexpectedError("Don't Retry"))
        }

        val result = executor.executeWithRetry(
            operation = operation,
            type = String::class.java,
            predicate = { it.apiError.message == "Retry" }
        )

        assertEquals(1, attempts)
        assertTrue(result is ApiResult.Error)
    }

    @Test
    fun `executeWithRetry handles NotSupported immediately`() = runTest {
        var attempts = 0
        val operation = suspend {
            attempts++
            ApiResult.NotSupported
        }

        val result = executor.executeWithRetry(
            operation = operation,
            type = String::class.java
        )

        assertEquals(1, attempts)
        assertEquals(ApiResult.NotSupported, result)
    }

    @Test
    fun `executeAndMap handles complex type transformations`() = runTest {
        data class Source(val id: Int, val name: String)
        data class Target(val display: String)

        val result = executor.executeAndMap(
            operation = { ApiResult.Success(Source(1, "test")) },
            sourceType = Source::class.java,
            targetType = Target::class.java
        ) { Target("${it.id}-${it.name}") }

        assertTrue(result is ApiResult.Success)
        assertEquals("1-test", (result as ApiResult.Success).data.display)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `handles timeouts`() = runTest {
        withContext(Dispatchers.Default.limitedParallelism(1)) {
            try {
                withTimeout(500) {
                    executor.execute({
                        delay(1000)
                        ApiResult.Success("too late")
                    }, String::class.java)
                }
                fail("Should have timed out")
            } catch (_: TimeoutCancellationException) {
                // Expected timeout
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `executeWithRetry respects delay between attempts`() = runTest {
        var attempts = 0
        var lastAttemptTime = 0L

        val result = executor.executeWithRetry(
            operation = {
                val currentTime = currentTime
                if (lastAttemptTime > 0) {
                    assertTrue("Minimum delay not respected", currentTime - lastAttemptTime >= 100)
                }
                lastAttemptTime = currentTime
                attempts++
                if (attempts < 3) ApiResult.Error(DefaultApiError.UnexpectedError())
                else ApiResult.Success("success")
            },
            type = String::class.java,
            maxAttempts = 3
        )

        assertEquals(3, attempts)
        assertTrue(result is ApiResult.Success)
    }

    @Test
    fun `executes operations with delays concurrently`() = runTest {
        val startTime = System.nanoTime()

        coroutineScope {
            listOf(
                async {
                    executor.execute(
                        { delay(100); ApiResult.Success("first") },
                        String::class.java
                    )
                },
                async {
                    executor.execute(
                        { delay(50); ApiResult.Success("second") },
                        String::class.java
                    )
                }
            ).awaitAll()
        }

        val totalTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)
        assertTrue("Operations should run concurrently", totalTime < 150)

        val results = executor.getResults(String::class.java)
        assertEquals(2, results.size)
        assertTrue(results.containsAll(listOf("first", "second")))
    }

    @Test
    fun `getErrors returns all errors`() = runTest {
        executor.execute({ ApiResult.Success("success") }, String::class.java)
        executor.execute({ ApiResult.Error(DefaultApiError.UnexpectedError()) }, Int::class.java)
        executor.execute({ ApiResult.NotSupported }, String::class.java)

        val errors = executor.getErrors()
        assertEquals(1, errors.size)
        assertTrue(errors[0].apiError is DefaultApiError.UnexpectedError)
    }

    @Test
    fun `getResult returns typed result`() = runTest {
        executor.execute({ ApiResult.Success("test") }, String::class.java)

        val result = executor.getResult(0, String::class.java)
        assertTrue(result is ApiResult.Success)
        assertEquals("test", (result as ApiResult.Success).data)

        assertNull(executor.getResult(0, Int::class.java))
        assertNull(executor.getResult(1, String::class.java))
    }

    @Test
    fun `reset clears all operations`() = runTest {
        executor.execute({ ApiResult.Success("test") }, String::class.java)
        executor.reset()

        assertTrue(executor.getResults(String::class.java).isEmpty())
        assertTrue(executor.getErrors().isEmpty())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `executeWithRetry uses exponential backoff`() = runTest {
        var attempts = 0
        var previousAttemptTime = 0L
        withContext(Dispatchers.Default.limitedParallelism(1)) {
            val startTime = System.currentTimeMillis()
            executor.executeWithRetry(
                operation = {
                    val currentTime = System.currentTimeMillis()
                    if (previousAttemptTime > 0) currentTime - previousAttemptTime else 0L
                    previousAttemptTime = currentTime

                    attempts++
                    if (attempts < 3) ApiResult.Error(DefaultApiError.UnexpectedError())
                    else ApiResult.Success("success")
                },
                type = String::class.java,
                initialDelay = 100,
                factor = 2.0
            )

            assertEquals(3, attempts)

            // Check that the actual delays between attempts follow exponential backoff
            val secondAttemptDelay = previousAttemptTime - startTime - 100 // first delay is 100ms
            assertTrue("Second attempt delay should be around 200ms", secondAttemptDelay in 180..220)
        }
    }

    @Test
    fun `executeAndCombine successfully combines results`() = runTest {
        data class Combined(val first: String, val second: Int)

        val result = executor.executeAndCombine(
            operations = listOf(
                { ApiResult.Success("test") },
                { ApiResult.Success(42) }
            ),
            type = Any::class.java
        ) { results ->
            Combined(
                results[0] as String,
                results[1] as Int
            )
        }

        assertTrue(result is ApiResult.Success)
        assertEquals("test", (result as ApiResult.Success).data.first)
        assertEquals(42, result.data.second)
    }

    @Test
    fun `executeAndCombine returns first error encountered`() = runTest {
        data class Combined(val first: String, val second: String)
        val expectedError = ApiResult.Error(DefaultApiError.UnexpectedError("Test error"))

        val result = executor.executeAndCombine(
            operations = listOf(
                { ApiResult.Success("test") },
                { expectedError }
            ),
            type = String::class.java
        ) { Combined(it[0], it[1]) }

        assertTrue(result is ApiResult.Error)
        assertEquals(expectedError.apiError.message, (result as ApiResult.Error).apiError.message)
    }

    @Test
    fun `executeAndCombine handles NotSupported result`() = runTest {
        val result = executor.executeAndCombine(
            operations = listOf(
                { ApiResult.Success("test") },
                { ApiResult.NotSupported }
            ),
            type = String::class.java
        ) { it.joinToString() }

        assertEquals(ApiResult.NotSupported, result)
    }

    @Test
    fun `executeAndCombine with NightVisionState example`() = runTest {
        data class SomeDataState(val isEnabled: Boolean, val useRedOverlay: Boolean)

        val result = executor.executeAndCombine(
            operations = listOf(
                { ApiResult.Success(true) },  // isEnabled
                { ApiResult.Success(false) }  // useRedOverlay
            ),
            type = Boolean::class.java
        ) { results ->
            SomeDataState(
                isEnabled = results[0],
                useRedOverlay = results[1]
            )
        }

        assertTrue(result is ApiResult.Success)
        with((result as ApiResult.Success).data) {
            assertTrue(isEnabled)
            assertFalse(useRedOverlay)
        }
    }

    @Test
    fun `executeAndCombine successfully combines multiple operations`() = runTest {
        data class CombinedState(val isEnabled: Boolean, val useFeature: Boolean)

        val result = executor.executeAndCombine(
            operations = listOf(
                { ApiResult.Success(true) },
                { ApiResult.Success(false) }
            ),
            type = Boolean::class.java
        ) { results ->
            CombinedState(
                isEnabled = results[0],
                useFeature = results[1]
            )
        }

        assertTrue(result is ApiResult.Success)
        with((result as ApiResult.Success).data) {
            assertTrue(isEnabled)
            assertFalse(useFeature)
        }
    }

    @Test
    fun `executeAndCombine returns NotSupported when encountered`() = runTest {
        val result = executor.executeAndCombine(
            operations = listOf(
                { ApiResult.Success(true) },
                { ApiResult.NotSupported }
            ),
            type = Boolean::class.java
        ) { results -> results[0] } // Combiner won't be called due to NotSupported

        assertEquals(ApiResult.NotSupported, result)
    }
}