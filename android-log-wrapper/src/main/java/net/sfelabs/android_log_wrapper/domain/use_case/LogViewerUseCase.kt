package net.sfelabs.android_log_wrapper.domain.use_case

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import net.sfelabs.android_log_wrapper.domain.model.LogLine
import net.sfelabs.android_log_wrapper.domain.repository.LogLineRepository
import net.sfelabs.core.ui.Resource
import net.sfelabs.core.ui.UiText
import javax.inject.Inject

class LogViewerUseCase @Inject constructor(
    private val repository: LogLineRepository
) {
    //Allows you to call this use case like it was a method.  ie logViewerUseCase()
    operator fun invoke(): Flow<Resource<List<LogLine>>> = flow {
        emit(Resource.Loading())
        val logLines = repository.getLogLines()
        emit(Resource.Success(logLines))
    }.catch {
        e -> emit(
            Resource.Error(
                uiText = UiText.DynamicString("An exception occurred: ${e.stackTrace}")
            )
        )
    }.flowOn(Dispatchers.IO)
}