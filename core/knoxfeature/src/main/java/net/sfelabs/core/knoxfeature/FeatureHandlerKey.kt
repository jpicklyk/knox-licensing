package net.sfelabs.core.knoxfeature

import dagger.MapKey
import net.sfelabs.core.knoxfeature.domain.model.FeatureKey
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class FeatureHandlerKey(val value: KClass<out FeatureKey<*>>)