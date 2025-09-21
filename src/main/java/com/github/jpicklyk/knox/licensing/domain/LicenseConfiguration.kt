package com.github.jpicklyk.knox.licensing.domain

data class LicenseConfiguration(
    val defaultKey: String,
    val namedKeys: Map<String, String> = emptyMap()
) {
    fun getKey(name: String = "default"): String {
        return when (name) {
            "default" -> defaultKey
            else -> namedKeys[name] ?: throw IllegalArgumentException("License key '$name' not found. Available keys: ${getAllKeyNames()}")
        }
    }

    fun getAllKeys(): Map<String, String> {
        return mapOf("default" to defaultKey) + namedKeys
    }

    fun getAllKeyNames(): Set<String> {
        return setOf("default") + namedKeys.keys
    }

    fun hasKey(name: String): Boolean {
        return name == "default" || namedKeys.containsKey(name)
    }
}