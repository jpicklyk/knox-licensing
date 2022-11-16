package net.sfelabs.android_log_wrapper.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.sfelabs.android_log_wrapper.data.SimpleLogLineRepository
import net.sfelabs.android_log_wrapper.domain.LogNode
import net.sfelabs.android_log_wrapper.domain.LogToRepositoryNode
import net.sfelabs.android_log_wrapper.domain.LogWrapper
import net.sfelabs.android_log_wrapper.domain.repository.LogLineRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object LoggingModule {
    @Provides
    @Singleton
    fun provideLogWrapper(repository: LogLineRepository): LogNode {
        return LogWrapper(
            nextNode = LogToRepositoryNode(
                nextNode = null,
                repository = repository
            )
        )
    }

    @Provides
    @Singleton
    fun provideSimpleLogLineRepository(): LogLineRepository {
        return SimpleLogLineRepository()
    }
/*
    @Provides
    @Singleton
    fun provideLogViewerUseCase(logLineRepository: LogLineRepository): LogViewerUseCase {
        return LogViewerUseCase(logLineRepository)
    }

    @Provides
    @Singleton
    fun provideStreamLogLinesUseCase(logLineRepository: LogLineRepository): StreamLogLinesUseCase {
        return StreamLogLinesUseCase(logLineRepository)
    }

 */
}