package net.sfelabs.knox_tactical.di

import javax.inject.Qualifier

/**
 * To avoid any injection collision with the standard Knox SDK when implementing DI, using
 * TacticalSdk Qualifier for all standard knox classes.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TacticalSdk
