package net.sfelabs.core.knox.feature.processor.utils

import java.util.Locale

fun String.capitalizeWords(): String {
    return split("_")
        .map { it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } }
        .joinToString("")
}