package net.sfelabs.core.knox.feature.api

import net.sfelabs.core.domain.usecase.model.ApiError

/**
 * Wrapper for PolicyState that helps maintain type safety and proper generic variance in the Feature system.
 *
 * This wrapper is necessary because:
 * 1. It allows us to use covariant type parameters (out T) which is required for proper type safety
 *    when dealing with different PolicyState implementations in collections and generic contexts
 * 2. Provides convenient access to common PolicyState properties without type casting
 * 3. Ensures type safety when dealing with generic Policy types in the FeatureRegistry and other
 *    components that need to handle multiple policy types
 *
 * Without this wrapper, we would either:
 * - Lose type information when storing features in collections
 * - Need unsafe casting when retrieving specific policy states
 * - Have difficulty maintaining proper generic variance in the Feature class
 *
 * The wrapper acts as a bridge between the generic Feature system and specific PolicyState
 * implementations while maintaining type safety and proper generic variance.
 */
data class PolicyStateWrapper<out T : PolicyState>(val value: T) {
    val isEnabled: Boolean get() = value.isEnabled
    val isSupported: Boolean get() = value.isSupported
    val error: ApiError? get() = value.error
    val exception: Throwable? get() = value.exception
}