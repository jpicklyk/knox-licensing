package net.sfelabs.core.knox.feature.processor.utils

object NameUtils {
    /**
     * Converts a class name to a feature name format.
     * Example: "AutoTouchSensitivityFeature" -> "auto_touch_sensitivity"
     */
    fun classNameToPolicyName(className: String): String {
        return className
            .removeSuffix("Policy")
            .replace(Regex("([a-z])([A-Z])"), "$1_$2")
            .lowercase()
    }
}