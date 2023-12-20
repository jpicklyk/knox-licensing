package net.sfelabs.core

import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.memberFunctions

fun checkMethodExistence(kotlinClass: KClass<*>, methodName: String): Boolean {
    try {



        val declaredFunctions = kotlinClass.declaredMemberFunctions
        val inheritedFunctions = kotlinClass.memberFunctions

        val allFunctions = declaredFunctions + inheritedFunctions

        for (function in allFunctions) {
            if (function.name == methodName) {
                return true
            }
        }
    } catch (e: ClassNotFoundException) {
        // Handle class not found exception
        e.printStackTrace()
    }

    return false
}