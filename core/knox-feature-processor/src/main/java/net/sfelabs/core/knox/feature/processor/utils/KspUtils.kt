package net.sfelabs.core.knox.feature.processor.utils

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.ClassName

fun KSType.toClassName(): ClassName {
    return when (val declaration = this.declaration) {
        is KSClassDeclaration -> getClassName(declaration)
        is KSTypeParameter -> {
            // For type parameters, use their first bound as the className
            // If no bounds are specified, default to Any
            val bound = declaration.bounds.firstOrNull()?.resolve()
            if (bound != null) {
                bound.toClassName()
            } else {
                ANY
            }
        }
        else -> throw IllegalStateException(
            "Unsupported type declaration: ${declaration::class.simpleName} for ${declaration.qualifiedName?.asString()}"
        )
    }
}

fun getClassName(declaration: KSClassDeclaration): ClassName {
    val qualifiedName = declaration.qualifiedName?.asString()
        ?: throw IllegalStateException("Could not resolve class name")
    return ClassName.bestGuess(qualifiedName)
}