package net.sfelabs.core.knoxfeature.domain

import net.sfelabs.core.domain.UiText
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.knoxfeature.domain.model.Feature
import net.sfelabs.core.knoxfeature.domain.model.FeatureKey
import net.sfelabs.core.knoxfeature.domain.model.FeatureState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeatureManager @Inject constructor(
    private val featureHandlerFactory: FeatureHandlerFactory,
    private val featureRegistry: FeatureRegistry
) {
    suspend fun <T> getFeatureState(feature: FeatureKey<T>): ApiResult<FeatureState<T>> {
        return featureHandlerFactory.getHandler(feature).getState()
    }

    suspend fun <T> setFeatureState(feature: FeatureKey<T>, newState: FeatureState<T>): ApiResult<Unit> {
        return featureHandlerFactory.getHandler(feature).setState(newState)
    }

    suspend fun getAllFeatures(category: FeatureCategory? = null): ApiResult<List<Feature<*>>> {
        return try {
            val features = featureRegistry.getFeatures(category).mapNotNull { featureKey ->
                when (val state = getFeatureState(featureKey)) {
                    is ApiResult.Success -> Feature(featureKey, state.data)
                    else -> null
                }
            }
            ApiResult.Success(features)
        } catch (e: Exception) {
            ApiResult.Error(UiText.DynamicString("Failed to get features"), exception = e)
        }
    }

    suspend fun getAllCategorizedFeatures(): ApiResult<Map<FeatureCategory, List<Feature<*>>>> {
        return try {
            val categorizedFeatures = featureRegistry.getCategorizedFeatures().mapValues { (_, featureKeys) ->
                featureKeys.mapNotNull { featureKey ->
                    when (val state = getFeatureState(featureKey)) {
                        is ApiResult.Success -> Feature(featureKey, state.data)
                        else -> null
                    }
                }
            }
            ApiResult.Success(categorizedFeatures)
        } catch (e: Exception) {
            ApiResult.Error(UiText.DynamicString("Failed to get categorized features"), exception = e)
        }
    }
}