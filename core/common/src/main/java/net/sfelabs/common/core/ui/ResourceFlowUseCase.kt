package net.sfelabs.common.core.ui

import kotlinx.coroutines.flow.Flow
import net.sfelabs.common.core.Resource

interface ResourceFlowUseCase {
    operator fun invoke(value: Any?): Flow<Resource<Any>>
}