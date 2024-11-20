package net.sfelabs.core.knoxfeature.domain.usecase.base

import net.sfelabs.core.domain.use_case.ApiUseCase
import net.sfelabs.core.knoxfeature.domain.FeatureHandler

interface FeatureUseCase<T, P, R : Any> : FeatureHandler<T>, ApiUseCase<P, R>