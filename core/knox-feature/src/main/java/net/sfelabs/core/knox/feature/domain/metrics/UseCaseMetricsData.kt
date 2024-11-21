package net.sfelabs.core.knox.feature.domain.metrics

data class UseCaseMetricsData(
    val averageDurationMs: Double,
    val successCount: Int,
    val errorCount: Int
)
