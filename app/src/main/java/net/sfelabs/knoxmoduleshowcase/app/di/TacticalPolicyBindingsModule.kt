package net.sfelabs.knoxmoduleshowcase.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ElementsIntoSet
import net.sfelabs.knox.core.feature.api.PolicyComponent
import net.sfelabs.knox.core.feature.api.PolicyState
import net.sfelabs.knox_tactical.generated.policy.GeneratedPolicyComponents

/**
 * Hilt module that provides Knox Tactical policy components.
 *
 * This module bridges the DI-agnostic [GeneratedPolicyComponents] registry
 * with Hilt's dependency injection system using multibindings.
 *
 * Note: This module is in the app because knox-tactical is a restricted module
 * not available to all customers. Customers using knox-tactical should add
 * a similar module in their own app.
 */
@Module
@InstallIn(SingletonComponent::class)
object TacticalPolicyBindingsModule {

    @Provides
    @ElementsIntoSet
    fun provideTacticalPolicies(): Set<@JvmSuppressWildcards PolicyComponent<out PolicyState>> =
        GeneratedPolicyComponents.getAll().toSet()
}
