package net.sfelabs.core.domain.usecase.executor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.model.DefaultApiError
import java.lang.ref.WeakReference
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * A builder for composing and executing use case operations with various execution patterns and control mechanisms.
 * This builder provides a fluent API for defining complex execution flows with support for sequential,
 * parallel, and conditional execution patterns.
 *
 * The builder is compatible with all UseCase implementations:
 * - [UseCase] interface implementations
 * - [BaseUseCase] implementations (synchronous with error mapping)
 * - [SuspendingUseCase] implementations (asynchronous with coroutine context switching)
 *
 * Memory Management:
 * - Error handlers are stored as WeakReferences to prevent memory leaks
 * - State change listeners use WeakReferences to allow proper garbage collection
 * - Automatic cleanup of resources after execution completes
 *
 * Coroutine Integration:
 * - Supports custom CoroutineScope via [withScope]
 * - Falls back to IO dispatcher scope if no scope provided
 * - Maintains proper coroutine hierarchy and cancellation
 *
 * State Management:
 * - Provides state change hooks via [onStateChanged]
 * - Tracks execution progress and results
 * - Reports operation success, failures, and skipped operations
 * - Automatic state cleanup after execution
 *
 * Key features:
 * - Sequential execution: Operations execute in order, stopping at first failure
 * - Parallel execution: Operations execute concurrently
 * - Any execution: Operations execute until first success
 * - Conditional execution using when/unless
 * - Retry policies with exponential backoff
 * - Timeout controls
 * - Error handling and fallbacks
 *
 * Android-Specific Considerations:
 * - All operations are executed on [Dispatchers.IO] by default to prevent main thread blocking
 * - No built-in lifecycle awareness - operations will continue even if the UI component is destroyed
 * - No automatic handling of configuration changes or process death
 * - Care should be taken with memory leaks when using error handlers or callbacks
 * - Long-running operation chains might need to consider battery optimization
 * - Parallel execution could potentially cause ANRs without proper timeout handling
 *
 * Threading Considerations:
 * - The builder executes its chain on [Dispatchers.IO] to prevent main thread blocking
 * - Coroutine dispatchers for individual operations should be specified in [SuspendingUseCase] implementations
 * - Parallel execution maintains proper coroutine scoping regardless of individual use case dispatchers
 *
 * Example with different UseCase types:
 * ```
 * val builder = UseCaseBuilder()
 * val results = builder.sequential { baseUseCase() }
 *     .withScope(lifecycleScope)
 *     .onStateChanged { state ->
 *         // Track execution progress
 *         state.executedOperations.forEach { operation ->
 *             if (operation.skipped) {
 *                 // Handle skipped operation
 *             } else if (operation.wasSuccessful) {
 *                 // Handle successful operation
 *             }
 *         }
 *     }
 *     .add { suspendingUseCase("param") }
 *     .then()
 *     .parallel()
 *     .add { useCase1(42) }
 *     .add { useCase2("test") }
 *     .execute()
 * ```
 *
 * Example sequential execution:
 * ```
 * val results = builder.sequential { useCase1() }
 *     .add { useCase2() }
 *     .withRetry(maxAttempts = 3)
 *     .then()
 *     .add { useCase3() }
 *     .execute()
 * ```
 *
 * Example parallel execution with fallback:
 * ```
 * val results = builder.parallel { useCase1() }
 *     .add { useCase2() }
 *     .withFallback { fallbackUseCase() }
 *     .withTimeout(5.seconds)
 *     .execute()
 * ```
 *
 * Example conditional execution:
 * ```
 * val results = builder.sequential { useCase1() }
 *     .add { useCase2() }
 *     .`when` { shouldExecuteMore() }
 *     .then()
 *     .add { useCase3() }
 *     .execute()
 * ```
 *
 * Example mixed execution patterns:
 * ```
 * val results = builder.sequential { setupUseCase() }
 *     .then()
 *     .parallel()
 *     .add { parallelUseCase1() }
 *     .add { parallelUseCase2() }
 *     .then()
 *     .add { finalizeUseCase() }
 *     .execute()
 * ```
 *
 * WorkManager Integration:
 * The builder is designed to be compatible with WorkManager for background processing:
 * - Built-in support for threading through [Dispatchers.IO]
 * - Configurable timeout and retry policies
 * - State tracking for progress monitoring
 * - Results can be easily mapped to WorkManager.Result
 *
 * Example WorkManager integration:
 * ```
 * class UseCaseWorker(
 *     context: Context,
 *     params: WorkerParameters
 * ) : CoroutineWorker(context, params) {
 *
 *     override suspend fun doWork(): Result {
 *         val builder = UseCaseBuilder()
 *
 *         return try {
 *             val results = builder.sequential { useCase1() }
 *                 .add { useCase2() }
 *                 .withTimeout(30.seconds)
 *                 .withRetry(maxAttempts = 3)
 *                 .execute()
 *
 *             if (results.all { it is ApiResult.Success }) {
 *                 Result.success()
 *             } else {
 *                 Result.failure()
 *             }
 *         } catch (e: Exception) {
 *             Result.failure()
 *         }
 *     }
 * }
 * ```
 *
 * All operations return [ApiResult] instances, and the builder maintains proper error
 * handling and cancellation support throughout the execution chain. Resources are automatically
 * cleaned up after execution completes.
 *
 * @see UseCase
 * @see BaseUseCase
 * @see SuspendingUseCase
 * @see ApiResult
 */

class UseCaseBuilder {
    internal sealed class ExecutionUnit {
        data class SingleOperation(
            val execute: suspend () -> ApiResult<*>,
            val predicate: (suspend () -> Boolean)? = null,
            val fallback: (suspend () -> ApiResult<*>)? = null,
            val errorHandler: WeakReference<((ApiResult.Error) -> Unit)>? = null,
            val retryPolicy: Builder.RetryPolicy? = null
        ) : ExecutionUnit()

        data class OperationGroup(
            val operations: MutableList<SingleOperation> = mutableListOf(),
            val type: GroupType = GroupType.SEQUENTIAL
        ) : ExecutionUnit()
    }

    internal enum class GroupType {
        SEQUENTIAL,    // All must succeed in order
        PARALLEL,      // All executed together
        ANY           // First success wins
    }

    interface UseCaseBuilderState {
        val executedOperations: List<ExecutedOperation>
        val currentResults: List<ApiResult<*>>
    }

    data class ExecutedOperation(
        val id: String,
        val timestamp: Long,
        val wasSuccessful: Boolean,
        val skipped: Boolean = false
    )

    class Builder {
        private var coroutineScope: WeakReference<CoroutineScope>? = null
        private val executionUnits = mutableListOf<ExecutionUnit>()
        private var currentGroup = ExecutionUnit.OperationGroup()
        private var timeoutDuration: Duration? = null
        private var retryPolicy: RetryPolicy? = null
        private var stateListener: WeakReference<((UseCaseBuilderState) -> Unit)>? = null
        private val executedOperations = mutableListOf<ExecutedOperation>()
        private val results = mutableListOf<ApiResult<*>>()

        data class RetryPolicy(
            val maxAttempts: Int = 3,
            val initialDelay: Duration = 100.milliseconds,
            val maxDelay: Duration = 500.milliseconds,
            val factor: Double = 2.0,
            val predicate: (ApiResult.Error) -> Boolean = { true }
        )

        fun onStateChanged(listener: (UseCaseBuilderState) -> Unit): Builder {
            stateListener = WeakReference(listener)
            return this
        }

        private fun notifyStateChanged() {
            val currentExecutedOperations = executedOperations.toList()
            val currentResults = results.toList()

            val state = object : UseCaseBuilderState {
                override val executedOperations: List<ExecutedOperation> = currentExecutedOperations
                override val currentResults: List<ApiResult<*>> = currentResults
            }
            stateListener?.get()?.invoke(state)
        }

        fun add(operation: suspend () -> ApiResult<*>): Builder {
            currentGroup.operations.add(
                ExecutionUnit.SingleOperation(operation)
            )
            return this
        }

        fun `when`(predicate: suspend () -> Boolean): Builder {
            val lastOp = currentGroup.operations.lastOrNull()
                ?: throw IllegalStateException("No operation to apply predicate to")
            currentGroup.operations[currentGroup.operations.lastIndex] =
                lastOp.copy(predicate = predicate)
            return this
        }

        fun unless(predicate: suspend () -> Boolean): Builder {
            return `when` { !predicate() }
        }

        fun withFallback(fallback: suspend () -> ApiResult<*>): Builder {
            val lastOp = currentGroup.operations.lastOrNull()
                ?: throw IllegalStateException("No operation to apply fallback to")
            currentGroup.operations[currentGroup.operations.lastIndex] =
                lastOp.copy(fallback = fallback)
            return this
        }

        fun withTimeout(duration: Duration): Builder {
            timeoutDuration = duration
            return this
        }

        fun withRetry(
            maxAttempts: Int = 3,
            initialDelay: Duration = 100.milliseconds,
            maxDelay: Duration = 500.milliseconds,
            factor: Double = 2.0,
            predicate: (ApiResult.Error) -> Boolean = { true }
        ): Builder {
            retryPolicy = RetryPolicy(maxAttempts, initialDelay, maxDelay, factor, predicate)
            return this
        }

        fun onError(handler: (ApiResult.Error) -> Unit): Builder {
            val lastOp = currentGroup.operations.lastOrNull()
                ?: throw IllegalStateException("No operation to apply error handler to")
            currentGroup.operations[currentGroup.operations.lastIndex] =
                lastOp.copy(errorHandler = WeakReference(handler))
            return this
        }

        fun any(): Builder {
            if (currentGroup.operations.isNotEmpty()) {
                executionUnits.add(currentGroup)
            }
            currentGroup = ExecutionUnit.OperationGroup(type = GroupType.ANY)
            return this
        }

        fun parallel(): Builder {
            if (currentGroup.operations.isNotEmpty()) {
                executionUnits.add(currentGroup)
            }
            currentGroup = ExecutionUnit.OperationGroup(type = GroupType.PARALLEL)
            return this
        }

        fun then(): Builder {
            if (currentGroup.operations.isNotEmpty()) {
                executionUnits.add(currentGroup)
            }
            currentGroup = ExecutionUnit.OperationGroup(type = GroupType.SEQUENTIAL)
            return this
        }

        fun withScope(scope: CoroutineScope): Builder {
            coroutineScope = WeakReference(scope)
            return this
        }

        suspend fun execute(): List<ApiResult<*>> {
            if (currentGroup.operations.isNotEmpty()) {
                executionUnits.add(currentGroup)
            }

            val scope = coroutineScope?.get() ?: CoroutineScope(Dispatchers.IO)

            return withContext(scope.coroutineContext) {  // Use the provided scope's context
                try {
                    withTimeoutOrNull(timeoutDuration?.inWholeMilliseconds ?: Long.MAX_VALUE) {
                        executeOperations()
                    } ?: emptyList()
                } finally {
                    cleanup()
                }
            }
        }

        private suspend fun executeSingleOperation(
            operation: ExecutionUnit.SingleOperation
        ): ApiResult<*> {
            // First add the result to our results list
            fun addResult(result: ApiResult<*>) {
                results.add(result)
                executedOperations.add(ExecutedOperation(
                    id = operation.hashCode().toString(),
                    timestamp = System.currentTimeMillis(),
                    wasSuccessful = result is ApiResult.Success,
                    skipped = false
                ))
                notifyStateChanged()
            }

            // Check predicate if exists
            if (operation.predicate?.invoke() == false) {
                val result = ApiResult.Success(Unit)
                results.add(result)
                executedOperations.add(ExecutedOperation(
                    id = operation.hashCode().toString(),
                    timestamp = System.currentTimeMillis(),
                    wasSuccessful = true,
                    skipped = true
                ))
                notifyStateChanged()
                return result
            }

            val result = executeWithRetry(operation)
            addResult(result)  // Add result and notify

            // Handle errors if handler exists
            if (result is ApiResult.Error) {
                operation.errorHandler?.get()?.invoke(result)
            }

            // Try fallback if main operation failed
            return when {
                result !is ApiResult.Success && operation.fallback != null -> {
                    val fallbackResult = operation.fallback.invoke()
                    // Track fallback execution
                    executedOperations.add(ExecutedOperation(
                        id = "${operation.hashCode()}_fallback",
                        timestamp = System.currentTimeMillis(),
                        wasSuccessful = fallbackResult is ApiResult.Success,
                        skipped = false
                    ))
                    results[results.lastIndex] = fallbackResult  // Replace the previous result
                    notifyStateChanged()
                    fallbackResult
                }
                else -> result
            }
        }

        private suspend fun executeWithRetry(
            operation: ExecutionUnit.SingleOperation
        ): ApiResult<*> {
            val policy = operation.retryPolicy ?: retryPolicy ?: return try {
                operation.execute()
            } catch (e: Throwable) {
                currentCoroutineContext().ensureActive()
                ApiResult.Error(DefaultApiError.UnexpectedError(), Exception(e))
            }

            var currentDelay = policy.initialDelay
            repeat(policy.maxAttempts) { attempt ->
                try {
                    val result = operation.execute()
                    when (result) {
                        is ApiResult.Success -> return result
                        is ApiResult.Error -> {
                            if (!policy.predicate(result) || attempt == policy.maxAttempts - 1) return result
                            currentCoroutineContext().ensureActive()
                            delay(currentDelay.inWholeMilliseconds)
                            currentDelay = (currentDelay.inWholeMilliseconds * policy.factor)
                                .milliseconds
                                .coerceAtMost(policy.maxDelay)
                        }
                        is ApiResult.NotSupported -> return result
                    }
                } catch (e: Throwable) {
                    currentCoroutineContext().ensureActive()
                    if (attempt == policy.maxAttempts - 1) {
                        return ApiResult.Error(DefaultApiError.UnexpectedError(), Exception(e))
                    }
                    delay(currentDelay.inWholeMilliseconds)
                    currentDelay = (currentDelay.inWholeMilliseconds * policy.factor)
                        .milliseconds
                        .coerceAtMost(policy.maxDelay)
                }
            }
            throw IllegalStateException("Unexpected state in executeWithRetry")
        }

        private suspend fun executeSequential(
            operations: List<ExecutionUnit.SingleOperation>
        ): List<ApiResult<*>> {
            val results = mutableListOf<ApiResult<*>>()
            for (operation in operations) {
                results.add(executeSingleOperation(operation))
                if (results.last() !is ApiResult.Success) break
            }
            return results
        }

        private suspend fun executeParallel(
            operations: List<ExecutionUnit.SingleOperation>
        ): List<ApiResult<*>> = coroutineScope {
            val results = operations.map { operation ->
                async { executeSingleOperation(operation) }
            }.awaitAll()

            // Ensure we have a final state update with all operations
            notifyStateChanged()

            results
        }

        private suspend fun executeAny(
            operations: List<ExecutionUnit.SingleOperation>
        ): List<ApiResult<*>> {
            val results = mutableListOf<ApiResult<*>>()

            for (operation in operations) {
                val result = executeSingleOperation(operation)
                results.add(result)
                if (result is ApiResult.Success) break
            }

            return results
        }

        private suspend fun executeOperations(): List<ApiResult<*>> {
            val results = mutableListOf<ApiResult<*>>()

            for (unit in executionUnits) {
                when (unit) {
                    is ExecutionUnit.SingleOperation -> {
                        results.add(executeSingleOperation(unit))
                        if (results.last() !is ApiResult.Success) break
                    }
                    is ExecutionUnit.OperationGroup -> {
                        val groupResults = when (unit.type) {
                            GroupType.SEQUENTIAL -> executeSequential(unit.operations)
                            GroupType.PARALLEL -> executeParallel(unit.operations)
                            GroupType.ANY -> executeAny(unit.operations)
                        }
                        results.addAll(groupResults)

                        if (unit.type == GroupType.SEQUENTIAL &&
                            groupResults.any { it !is ApiResult.Success }) {
                            break
                        }
                    }
                }
            }

            return results
        }

        private fun cleanup() {
            executedOperations.clear()
            results.clear()
            stateListener = null
            coroutineScope = null
        }
    }

    fun sequential(operation: suspend () -> ApiResult<*>): Builder {
        return Builder().add(operation)
    }

    fun parallel(operation: suspend () -> ApiResult<*>): Builder {
        return Builder().parallel().add(operation)
    }

    fun any(operation: suspend () -> ApiResult<*>): Builder {
        return Builder().any().add(operation)
    }
}