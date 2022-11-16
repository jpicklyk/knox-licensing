package net.sfelabs.android_log_wrapper.domain.use_case

import kotlinx.coroutines.flow.Flow
import net.sfelabs.android_log_wrapper.domain.model.LogLine
import net.sfelabs.android_log_wrapper.domain.repository.LogLineRepository
import javax.inject.Inject

class StreamLogLinesUseCase @Inject constructor(
    private val repository: LogLineRepository
) {

    operator fun invoke(): Flow<LogLine> = repository.getLogStream()

}