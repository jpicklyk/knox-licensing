package net.sfelabs.core.knoxfeature.domain.metrics

import java.util.concurrent.atomic.AtomicInteger

class UseCaseMetrics {
    private val durations = mutableMapOf<String, MutableList<Long>>()
    private val successCount = mutableMapOf<String, AtomicInteger>()
    private val errorCount = mutableMapOf<String, AtomicInteger>()

    fun recordDuration(useCaseName: String, durationMs: Long) {
        durations.getOrPut(useCaseName) { mutableListOf() }.add(durationMs)
    }

    fun recordSuccess(useCaseName: String) {
        successCount.getOrPut(useCaseName) { AtomicInteger(0) }.incrementAndGet()
    }

    fun recordError(useCaseName: String) {
        errorCount.getOrPut(useCaseName) { AtomicInteger(0) }.incrementAndGet()
    }

    fun getMetrics(useCaseName: String): UseCaseMetricsData {
        val durationsList = durations[useCaseName] ?: emptyList()
        return UseCaseMetricsData(
            averageDurationMs = durationsList.average(),
            successCount = successCount[useCaseName]?.get() ?: 0,
            errorCount = errorCount[useCaseName]?.get() ?: 0
        )
    }
}