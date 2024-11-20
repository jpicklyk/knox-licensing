package net.sfelabs.core.knoxfeature.domain.metrics

data class UseCaseMetricsData(
    val averageDurationMs: Double,
    val successCount: Int,
    val errorCount: Int
)
