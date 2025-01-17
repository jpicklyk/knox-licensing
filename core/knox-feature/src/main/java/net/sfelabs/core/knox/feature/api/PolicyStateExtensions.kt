package net.sfelabs.core.knox.feature.api

/**
 * Checks if the policy state contains an error.
 *
 * @return true if there is an error present, false otherwise
 */
fun PolicyState.hasError() = error != null

/**
 * Checks if the policy state represents a successful operation.
 *
 * @return true if there are no errors present, false otherwise
 */
fun PolicyState.isSuccessful() = error == null