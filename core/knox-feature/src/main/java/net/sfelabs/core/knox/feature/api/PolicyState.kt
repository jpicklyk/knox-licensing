package net.sfelabs.core.knox.feature.api

import net.sfelabs.core.domain.usecase.model.ApiError

/**
 * Base interface for all policy states in the Knox policy feature system.
 *
 * PolicyState defines the core properties that every policy must implement, regardless of its specific
 * configuration or functionality. This interface ensures consistent error handling, support status checking,
 * and state management across all policies.
 *
 * Core Properties:
 * - isEnabled: Whether the policy is currently active
 * - isSupported: Whether the policy is supported on the current device
 * - error: Any error that occurred during policy operations
 * - exception: Detailed exception information if available
 *
 * Core Functions:
 * - withError: Creates a new state instance with updated error information.  This allows for the
 *              reduction of boilerplate code in policy implementations by the direct use of the
 *              data class copy function.
 *
 * Specific policy implementations should:
 * 1. Implement this interface in their state data classes
 * 2. Add any additional properties needed for their specific configuration
 * 3. Provide appropriate default values for the core properties
 * 4. Implement withError to return a new instance with updated error information
 *
 * Example implementation:
 * ```
 * data class BandLockingState(
 *     override val isEnabled: Boolean,
 *     override val isSupported: Boolean = true,
 *     override val error: ApiError? = null,
 *     override val exception: Throwable? = null,
 *     val band: Int,                    // Policy-specific configuration
 *     val simSlotId: Int? = null        // Policy-specific configuration
 * ) : PolicyState {
 *     override fun withError(error: ApiError?, exception: Throwable?): PolicyState {
 *         return copy(error = error, exception = exception)
 *     }
 * }
 * ```
 *
 * @see FeatureContract
 * @see PolicyStateWrapper
 */
interface PolicyState {
    val isEnabled: Boolean
    val isSupported: Boolean
    val error: ApiError?
    val exception: Throwable?

    fun withError(error: ApiError?, exception: Throwable?): PolicyState
}