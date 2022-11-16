package net.sfelabs.knox_tactical.domain.use_cases

import kotlin.reflect.KClass

class KnoxFunctionProbeUseCase {

    operator fun invoke(classPackageName: String, functionName: String): Boolean {
        val parent = Class.forName(classPackageName).kotlin
        return doesFunctionExist(parent,functionName)
    }

    operator fun invoke(className: KClass<out Any>, functionName: String): Boolean {
        return doesFunctionExist(className, functionName)
    }

    private fun doesFunctionExist(className: KClass<out Any>, functionName: String): Boolean {
        val function =  className.members.find{ it.name.equals(functionName, ignoreCase = true) }
        return (function != null)
    }
}