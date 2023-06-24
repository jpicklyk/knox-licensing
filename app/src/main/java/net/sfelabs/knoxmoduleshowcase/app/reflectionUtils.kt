package net.sfelabs.knoxmoduleshowcase.app
import kotlin.reflect.KClass
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.declaredMemberFunctions

fun checkMethodExistence(className: String, methodName: String): Boolean {
    val clazz = Class.forName(className)
    val kotlinClass = clazz.kotlin
    return checkMethodExistence(kotlinClass, methodName)
}
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