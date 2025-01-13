package net.sfelabs.core.domain.usecase.executor

import kotlinx.coroutines.delay
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.model.DefaultApiError

/**
 * A utility class for executing and managing API operations with various execution strategies.
 *
 * Supports robust API call handling including:
 * - Retrying operations with exponential backoff
 * - Fallback mechanisms
 * - Error handling
 * - Conditional execution
 * - Result tracking and retrieval
 *
 * @property operations Internal storage for tracked API operation results
 */
@Deprecated("Please use `UseCaseBuilder` instead.")
class UseCaseExecutor {
    /**
     * Internal representation of a stored API operation
     */
    private sealed class Operation<T : Any> {
        data class Stored<T : Any>(val result: ApiResult<T>, val type: Class<T>) : Operation<T>()
    }

    private val operations = mutableListOf<Operation<*>>()

    /**
     * Retrieves successful results for a specific type
     * @param type Class type of results to retrieve
     * @return List of successfully executed data results
     */
    fun <T : Any> getResults(type: Class<T>): List<T> = operations
        .filterIsInstance<Operation.Stored<T>>()
        .filter { it.type == type }
        .mapNotNull { (it.result as? ApiResult.Success)?.data }

    /**
     * Retrieves all error results from executed operations
     * @return List of ApiResult.Error instances
     */
    fun getErrors(): List<ApiResult.Error> = operations
        .mapNotNull { (it as? Operation.Stored<*>)?.result as? ApiResult.Error }

    /**
     * Retrieves a specific result by index and type
     * @param index Operation index
     * @param type Expected result type
     * @return ApiResult matching the type, or null
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getResult(index: Int, type: Class<T>): ApiResult<T>? =
        (operations.getOrNull(index) as? Operation.Stored<*>)?.let { stored ->
            if (stored.type == type) stored.result as? ApiResult<T> else null
        }

    /**
     * Clears all stored operations
     */
    fun reset() = operations.clear()

    /**
     * Executes an API operation and stores its result
     * @param operation Suspend function to execute
     * @param type Result type class
     * @return Operation result
     */
    suspend fun <T : Any> execute(operation: suspend () -> ApiResult<T>, type: Class<T>): ApiResult<T> {
        val result = operation()
        operations.add(Operation.Stored(result, type))
        return result
    }

    /**
     * Executes an operation with retry mechanism using exponential backoff
     * @param operation Suspend function to execute
     * @param type Result type class
     * @param maxAttempts Maximum retry attempts (default 3)
     * @param initialDelay Initial retry delay (default 100ms)
     * @param maxDelay Maximum retry delay (default 500ms)
     * @param factor Backoff factor (default 2.0)
     * @param predicate Condition for retry attempts
     * @return Final operation result
     */
    suspend fun <T : Any> executeWithRetry(
        operation: suspend () -> ApiResult<T>,
        type: Class<T>,
        maxAttempts: Int = 3,
        initialDelay: Long = 100,
        maxDelay: Long = 500,
        factor: Double = 2.0,
        predicate: (ApiResult.Error) -> Boolean = { true }
    ): ApiResult<T> {
        var currentDelay = initialDelay
        repeat(maxAttempts) { attempt ->
            when (val result = execute(operation, type)) {
                is ApiResult.Success -> return result
                is ApiResult.Error -> {
                    if (!predicate(result) || attempt == maxAttempts - 1) return result
                    delay(currentDelay)
                    currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
                }
                is ApiResult.NotSupported -> return result
            }
        }
        throw IllegalStateException("Unexpected state in executeWithRetry")
    }

    /**
     * Executes primary operation with fallback option
     * @param primaryOperation Primary suspend function
     * @param fallbackOperation Fallback suspend function
     * @param type Result type class
     * @return Result from primary or fallback operation
     */
    suspend fun <T : Any> executeWithFallback(
        primaryOperation: suspend () -> ApiResult<T>,
        fallbackOperation: suspend () -> ApiResult<T>,
        type: Class<T>
    ): ApiResult<T> {
        return when (val result = execute(primaryOperation, type)) {
            is ApiResult.Success -> result
            else -> execute(fallbackOperation, type)
        }
    }

    /**
     * Executes operation and maps result to another type
     * @param operation Suspend function to execute
     * @param sourceType Original result type
     * @param targetType Mapped result type
     * @param mapper Transformation function
     * @return Mapped operation result
     */
    suspend fun <T : Any, R : Any> executeAndMap(
        operation: suspend () -> ApiResult<T>,
        sourceType: Class<T>,
        targetType: Class<R>,
        mapper: (T) -> R
    ): ApiResult<R> {
        return when (val result = execute(operation, sourceType)) {
            is ApiResult.Success -> ApiResult.Success(mapper(result.data))
            is ApiResult.Error -> result
            is ApiResult.NotSupported -> result
        }
    }

    /**
     * Executes operation with custom error handling
     * @param operation Suspend function to execute
     * @param type Result type class
     * @param errorHandler Error handling function
     * @return Successful data or null
     */
    suspend fun <T : Any> executeWithErrorHandler(
        operation: suspend () -> ApiResult<T>,
        type: Class<T>,
        errorHandler: (ApiResult.Error) -> Unit
    ): T? {
        return when (val result = execute(operation, type)) {
            is ApiResult.Success -> result.data
            is ApiResult.Error -> {
                errorHandler(result)
                null
            }
            is ApiResult.NotSupported -> null
        }
    }

    /**
     * Conditionally executes an operation
     * @param condition Execution condition
     * @param operation Suspend function to execute
     * @param type Result type class
     * @return Operation result or null
     */
    suspend fun <T : Any> executeIf(
        condition: Boolean,
        operation: suspend () -> ApiResult<T>,
        type: Class<T>
    ): ApiResult<T>? {
        return if (condition) execute(operation, type) else null
    }

    /**
     * Executes operation with logging (currently commented out)
     * @param tag Logging tag
     * @param operation Suspend function to execute
     * @param type Result type class
     * @return Operation result
     */
    suspend fun <T : Any> executeWithLogging(
        tag: String,
        operation: suspend () -> ApiResult<T>,
        type: Class<T>
    ): ApiResult<T> {
        //Log.d(tag, "Executing operation")
        val result = execute(operation, type)
        //Log.d(tag, "Operation result: $result")
        return result
    }

    /**
     * Executes multiple operations of the same type and combines their results.
     *
     * This function will:
     * 1. Execute all operations in sequence
     * 2. Return immediately if any operation results in an error
     * 3. Combine all successful results using the provided combiner function
     *
     * @param T The type of data returned by individual operations
     * @param R The type of the combined result
     * @param operations List of operations to execute
     * @param type Class type of individual operation results
     * @param combiner Function to combine successful results into final type
     * @return ApiResult.Success with combined result if all operations succeed,
     *         ApiResult.Error from first failed operation, or
     *         ApiResult.Error with UnexpectedError if results count doesn't match operations
     */
    suspend fun <T : Any, R : Any> executeAndCombine(
        operations: List<suspend () -> ApiResult<T>>,
        type: Class<T>,
        combiner: (List<T>) -> R
    ): ApiResult<R> {
        operations.forEach { operation ->
            when (val result = execute(operation, type)) {
                is ApiResult.NotSupported -> return ApiResult.NotSupported
                else -> Unit // Continue execution
            }
        }

        // Return first error if any operation failed
        getErrors().firstOrNull()?.let { return it }

        val results = getResults(type)
        return if (results.size == operations.size) {
            ApiResult.Success(combiner(results))
        } else {
            ApiResult.Error(DefaultApiError.UnexpectedError())
        }
    }
}