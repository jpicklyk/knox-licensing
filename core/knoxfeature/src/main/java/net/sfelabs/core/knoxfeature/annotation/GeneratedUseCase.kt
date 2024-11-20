package net.sfelabs.core.knoxfeature.annotation

@Target(AnnotationTarget.CLASS)
annotation class GeneratedUseCase(
    val name: String,
    val withMetrics: Boolean = true,
    val defaultBlocking: Boolean = false
)
