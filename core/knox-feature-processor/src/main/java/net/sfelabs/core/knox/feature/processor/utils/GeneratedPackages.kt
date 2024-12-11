package net.sfelabs.core.knox.feature.processor.utils

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

object GeneratedPackages {
    private fun getNamespace(environment: SymbolProcessorEnvironment): String {
        return environment.options["android.namespace"]
            ?: throw IllegalStateException("Android namespace not provided in KSP arguments")
    }

    fun getFeaturePackage(environment: SymbolProcessorEnvironment): String =
        "${getNamespace(environment)}.generated.feature"

    fun getDiPackage(environment: SymbolProcessorEnvironment): String =
        "${getNamespace(environment)}.generated.di"
}