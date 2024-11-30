package net.sfelabs.core.knox.feature.processor.utils

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName

fun KSType.toClassName(): ClassName {
    return when (val declaration = this.declaration) {
        is KSClassDeclaration -> getClassName(declaration)
        else -> throw IllegalStateException("Type declaration is not a class declaration")
    }
}

fun getClassName(declaration: KSClassDeclaration): ClassName {
    val qualifiedName = declaration.qualifiedName?.asString()
        ?: throw IllegalStateException("Could not resolve class name")
    return ClassName.bestGuess(qualifiedName)
}