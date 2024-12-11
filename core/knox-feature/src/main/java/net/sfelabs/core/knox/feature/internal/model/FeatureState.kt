package net.sfelabs.core.knox.feature.internal.model

import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.feature.internal.component.StateMapping

data class FeatureState<out T>(val enabled: Boolean, val value: T)

fun <T : Any> ApiResult<T>.wrapInFeatureState(stateMapping: StateMapping): ApiResult<FeatureState<T>> {
    return when (this) {
        is ApiResult.Success -> {
            val enabled = when (stateMapping) {
                StateMapping.DIRECT -> if (data is Boolean) data else true
                StateMapping.INVERTED -> if (data is Boolean) !(data as Boolean) else true
                StateMapping.CUSTOM -> {
                    if (data is Boolean) {
                        try {
                            val className = this::class.java.name
                            val packageName = className.substring(0, className.lastIndexOf('.'))
                            val companionClass = Class.forName("$packageName.Companion")
                            val mapStateMethod = companionClass.getDeclaredMethod("mapState", Boolean::class.java)
                            mapStateMethod.invoke(null, data) as Boolean
                        } catch (_: Exception) {
                            data  // fallback to direct mapping
                        }
                    } else {
                        true
                    }
                }
            }
            ApiResult.Success(FeatureState(enabled as Boolean, data))
        }
        is ApiResult.Error -> this
        is ApiResult.NotSupported -> this
    }
}