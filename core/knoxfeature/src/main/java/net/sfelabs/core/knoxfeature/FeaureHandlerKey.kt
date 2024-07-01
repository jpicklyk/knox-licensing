package net.sfelabs.core.knoxfeature

import dagger.MapKey
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class FeatureHandlerKey(val value: KClass<out FeatureKey<*>>)