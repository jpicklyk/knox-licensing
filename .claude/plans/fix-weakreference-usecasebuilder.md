# Fix WeakReference Usage in UseCaseBuilder

**Status:** ✅ COMPLETE
**Created:** 2026-01-30
**Related Task:** #5 - Fix WeakReference usage in UseCaseBuilder

## Problem Summary

The `UseCaseBuilder` class uses `WeakReference` for callbacks and scope references, which can lead to silent failures when garbage collection occurs during execution.

## Current WeakReference Usage

### Location: `knox-core/usecase-executor/src/main/java/net/sfelabs/knox/core/domain/usecase/executor/UseCaseBuilder.kt`

| Line | Field/Usage | Type |
|------|-------------|------|
| 183 | `errorHandler` in `SingleOperation` | `WeakReference<((ApiResult.Error) -> Unit)>?` |
| 212 | `coroutineScope` in `Builder` | `WeakReference<CoroutineScope>?` |
| 217 | `stateListener` in `Builder` | `WeakReference<((UseCaseBuilderState) -> Unit)>?` |
| 242 | `stateListener?.get()?.invoke(state)` | Usage - can silently fail |
| 330 | `coroutineScope?.get()` | Usage - falls back to orphan scope |
| 377 | `errorHandler?.get()?.invoke(result)` | Usage - can silently fail |

## Issues Identified

### 1. Premature Garbage Collection
Lambdas passed to `onError()` and `onStateChanged()` are stored as WeakReferences. If the caller doesn't maintain a strong reference, GC can collect them before execution completes.

```kotlin
// This lambda may be garbage collected before execute() completes
builder.sequential { ... }
    .onStateChanged { state -> updateUI(state) }  // WeakReference - may be GC'd
    .execute()
```

### 2. Silent Failures
When references are collected, the code silently does nothing:

```kotlin
// Line 242 - silently skips notification if listener was GC'd
stateListener?.get()?.invoke(state)

// Line 377 - silently skips error handling if handler was GC'd
operation.errorHandler?.get()?.invoke(result)
```

### 3. Inconsistent Behavior
- Callbacks work sometimes, fail silently other times
- Depends on GC timing - extremely hard to debug
- Tests may pass while production fails

### 4. Orphan CoroutineScope Creation
```kotlin
// Line 330 - if WeakReference is collected, creates orphan scope
val scope = coroutineScope?.get() ?: CoroutineScope(Dispatchers.IO)
```

This fallback scope:
- Has no parent (not structured concurrency)
- Won't be cancelled if the original scope was cancelled
- Defeats the purpose of `withScope()`

### 5. Incorrect Use Case for WeakReference
WeakReferences are designed for:
- Caching where data can be regenerated
- Breaking reference cycles
- Holding references to long-lived objects

They are NOT appropriate for:
- Short-lived callbacks that must execute
- Builder patterns where execution is immediate
- Lambdas created inline that have no other references

## Root Cause Analysis

The KDoc mentions:
> Memory Management:
> - Error handlers are stored as WeakReferences to prevent memory leaks
> - State change listeners use WeakReferences to allow proper garbage collection

The concern appears to be Android memory leaks (e.g., holding Activity references). However:

1. The builder execution is typically immediate (suspend function)
2. The `cleanup()` method already nulls all references after execution
3. WeakReferences cause more problems than they solve here

## Recommended Solution

### Option A: Remove WeakReferences (Recommended)

Replace WeakReferences with regular references since:
- Execution is synchronous from caller's perspective
- `cleanup()` already handles resource cleanup
- Short-lived builder pattern doesn't need weak refs

**Changes:**

```kotlin
// Before
private var stateListener: WeakReference<((UseCaseBuilderState) -> Unit)>? = null
private var coroutineScope: WeakReference<CoroutineScope>? = null

data class SingleOperation(
    // ...
    val errorHandler: WeakReference<((ApiResult.Error) -> Unit)>? = null,
)

// After
private var stateListener: ((UseCaseBuilderState) -> Unit)? = null
private var coroutineScope: CoroutineScope? = null

data class SingleOperation(
    // ...
    val errorHandler: ((ApiResult.Error) -> Unit)? = null,
)
```

**Update usages:**

```kotlin
// Before
stateListener?.get()?.invoke(state)
coroutineScope?.get() ?: CoroutineScope(Dispatchers.IO)
operation.errorHandler?.get()?.invoke(result)

// After
stateListener?.invoke(state)
coroutineScope ?: CoroutineScope(Dispatchers.IO)
operation.errorHandler?.invoke(result)
```

### Option B: Document Lifecycle Concerns

If keeping WeakReferences, add warnings to KDoc:

```kotlin
/**
 * @warning The listener is stored as a WeakReference. Callers must maintain
 * a strong reference to the listener for the duration of execution, or
 * state changes may be silently dropped.
 */
fun onStateChanged(listener: (UseCaseBuilderState) -> Unit): Builder
```

**Not recommended** - doesn't fix the underlying issue.

### Option C: Return State in Results

Instead of callbacks, return state information in the execution result:

```kotlin
data class ExecutionResult(
    val results: List<ApiResult<*>>,
    val executedOperations: List<ExecutedOperation>,
    val errors: List<ApiResult.Error>
)

suspend fun execute(): ExecutionResult
```

**Consideration:** This is a larger API change but eliminates callback issues entirely.

## Implementation Plan

### Phase 1: Remove WeakReferences ✅

- [x] Change `stateListener` from `WeakReference<...>?` to direct type
- [x] Change `coroutineScope` from `WeakReference<...>?` to direct type
- [x] Change `errorHandler` in `SingleOperation` from `WeakReference<...>?` to direct type
- [x] Update all `.get()` usages to direct invocation
- [x] Update KDoc to remove WeakReference mentions
- [x] Remove `java.lang.ref.WeakReference` import

### Phase 2: Update Tests ✅

- [x] Verify existing tests still pass (all 18 tests pass)

### Phase 3: Documentation ✅

- [x] Updated class KDoc about memory management
- [x] Added guidance for Android lifecycle concerns (use viewModelScope/lifecycleScope)

## Files to Modify

| File | Changes |
|------|---------|
| `UseCaseBuilder.kt` | Remove WeakReference usage, update types and invocations |
| `UseCaseBuilderTest.kt` | Add reliability tests |

## Risks and Mitigations

| Risk | Mitigation |
|------|------------|
| Memory leaks with Activity references | Document to use lifecycle-aware scopes; `cleanup()` already nulls references |
| Breaking existing callers | API remains the same, only internal implementation changes |
| Test failures | Existing tests should pass; behavior becomes more reliable |

## Success Criteria

1. All callbacks are reliably invoked (no silent failures)
2. CoroutineScope is properly respected (no orphan scopes)
3. All existing tests pass
4. No memory leaks (verified by cleanup behavior)

## Alternative Considered: Keep WeakReference with Strong Reference Holder

```kotlin
class Builder {
    // Keep strong refs internally to prevent GC
    private val strongRefs = mutableListOf<Any>()

    fun onStateChanged(listener: (UseCaseBuilderState) -> Unit): Builder {
        strongRefs.add(listener)  // Prevent GC
        stateListener = WeakReference(listener)
        return this
    }
}
```

**Rejected:** This defeats the purpose of WeakReference and adds unnecessary complexity.

## Notes

- The `cleanup()` method at line 505-510 already handles resource cleanup after execution
- The import for `java.lang.ref.WeakReference` can be removed after changes
- Consider adding `@VisibleForTesting` to internal classes if needed for testing
