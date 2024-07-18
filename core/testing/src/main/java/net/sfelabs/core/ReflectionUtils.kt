package net.sfelabs.core

import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.memberFunctions

fun checkMethodExistence(kotlinClass: KClass<*>, methodName: String): Boolean {
    return runCatching {
        kotlinClass.run {
            (declaredMemberFunctions + memberFunctions)
                .any { it.name.contains(methodName, ignoreCase = true) }
        }
    }.getOrElse { e ->
        when (e) {
            is ClassNotFoundException -> {
                println("Class not found: ${e.message}")
                e.printStackTrace()
            }
            else -> println("An unexpected error occurred: ${e.message}")
        }
        false
    }
}