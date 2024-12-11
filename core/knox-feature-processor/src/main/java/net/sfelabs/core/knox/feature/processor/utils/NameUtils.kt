package net.sfelabs.core.knox.feature.processor.utils

object NameUtils {
    /**
     * Converts a class name to a feature name format.
     * Example: "AutoTouchSensitivityFeature" -> "auto_touch_sensitivity"
     */
    fun classNameToFeatureName(className: String): String {
        return className
            .removeSuffix("Feature")
            .replace(Regex("([a-z])([A-Z])"), "$1_$2")
            .lowercase()
    }
}