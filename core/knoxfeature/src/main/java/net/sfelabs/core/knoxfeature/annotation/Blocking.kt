package net.sfelabs.core.knoxfeature.annotation

@Target(AnnotationTarget.FUNCTION)
annotation class Blocking (
    val timeoutMs: Long = 5000
)