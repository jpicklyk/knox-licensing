package net.sfelabs.core.knoxfeature.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.*

class GeneratedFeatureUseCaseProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) {
    fun generateFeatureUseCase(classDecl: KSClassDeclaration) {
        logger.error("Starting to process: ${classDecl.qualifiedName?.asString()}")

        val className = classDecl.simpleName.asString()
        val isGetter = className.startsWith("Get")
        val isSetter = className.startsWith("Set")

        logger.info("Class type: ${if (isGetter) "Getter" else if (isSetter) "Setter" else "Standard"}")

        val annotation = classDecl.annotations.first {
            it.shortName.asString() == "GeneratedFeatureUseCase"
        }

        val feature = (annotation.arguments.first {
            it.name?.asString() == "feature"
        }.value as KSClassDeclaration).asType(emptyList())

        val defaultBlocking = annotation.arguments.first {
            it.name?.asString() == "defaultBlocking"
        }.value as Boolean

        val (featureType, paramType, returnType) = getFeatureUseCaseTypes(
            classDecl.superTypes.first()
        )

        logger.info("""
            Generating use case with:
            - Type: ${if (isGetter) "Getter" else if (isSetter) "Setter" else "Standard"}
            - Feature: ${feature.declaration.qualifiedName?.asString()}
            - Feature Type: $featureType
            - Param Type: $paramType
            - Return Type: $returnType
            - Blocking: $defaultBlocking
        """.trimIndent())

        when {
            isGetter -> generateGetterImplementation(classDecl, defaultBlocking, paramType, returnType)
            isSetter -> generateSetterImplementation(classDecl, defaultBlocking, paramType, returnType)
            else -> generateStandardImplementation(classDecl, defaultBlocking, paramType, returnType)
        }
        logger.info("Completed processing: ${classDecl.qualifiedName?.asString()}")

    }

    private fun generateGetterImplementation(
        classDecl: KSClassDeclaration,
        defaultBlocking: Boolean,
        paramType: TypeName,
        returnType: TypeName
    ) {
        val fileSpec = FileSpec.builder(
            classDecl.packageName.asString(),
            "${classDecl.simpleName.asString()}Generated"
        )
            .addType(
                TypeSpec.classBuilder("${classDecl.simpleName.asString()}Generated")
                    .superclass(classDecl.asType(emptyList()).toTypeName())
                    .addType(generateBuilderClass(classDecl, paramType, returnType))
                    .addFunction(
                        FunSpec.builder("executeEnabled")
                            .addModifiers(KModifier.OVERRIDE, KModifier.SUSPEND)
                            .addParameter("params", paramType)
                            .addParameter("state", ClassName("kotlin", "Boolean"))
                            .returns(
                                ClassName("net.sfelabs.core.domain.api", "ApiResult")
                                    .parameterizedBy(returnType)
                            )
                            .addCode("""
                       ${if (defaultBlocking) "return withContext(dispatcher ?: defaultDispatcher) {" else "return"}
                           try {
                               val result = restrictionPolicy.isTacticalDeviceModeEnabled
                               ApiResult.Success(result)
                           } catch (e: SecurityException) {
                               mapError(e)
                           } catch (nsm: NoSuchMethodError) {
                               ApiResult.NotSupported
                           }
                       ${if (defaultBlocking) "}" else ""}
                   """.trimIndent())
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("executeDisabled")
                            .addModifiers(KModifier.OVERRIDE, KModifier.SUSPEND)
                            .addParameter("params", paramType)
                            .addParameter("state", ClassName("kotlin", "Boolean"))
                            .returns(
                                ClassName("net.sfelabs.core.domain.api", "ApiResult")
                                    .parameterizedBy(returnType)
                            )
                            .addCode("""
                       return ApiResult.Error(UiText.DynamicString("Feature is disabled"))
                   """.trimIndent())
                            .build()
                    )
                    .build()
            )
            .build()

        fileSpec.writeTo(codeGenerator, Dependencies(true, classDecl.containingFile!!))
    }

    private fun generateSetterImplementation(
        classDecl: KSClassDeclaration,
        defaultBlocking: Boolean,
        paramType: TypeName,
        returnType: TypeName
    ) {
        val fileSpec = FileSpec.builder(
            classDecl.packageName.asString(),
            "${classDecl.simpleName.asString()}Generated"
        )
            .addType(
                TypeSpec.classBuilder("${classDecl.simpleName.asString()}Generated")
                    .superclass(classDecl.asType(emptyList()).toTypeName())
                    .addType(generateBuilderClass(classDecl, paramType, returnType))
                    .addFunction(
                        FunSpec.builder("executeEnabled")
                            .addModifiers(KModifier.OVERRIDE, KModifier.SUSPEND)
                            .addParameter("params", paramType)
                            .addParameter("state", ClassName("kotlin", "Boolean"))
                            .returns(
                                ClassName("net.sfelabs.core.domain.api", "ApiResult")
                                    .parameterizedBy(returnType)
                            )
                            .addCode("""
                        ${if (defaultBlocking) "return withContext(dispatcher ?: defaultDispatcher) {" else "return"}
                            try {
                                val success = restrictionPolicy.enableTacticalDeviceMode(params)
                                if (success) ApiResult.Success(Unit)
                                else ApiResult.Error(UiText.DynamicString("Failed to set tactical device mode"))
                            } catch (e: SecurityException) {
                                mapError(e)
                            } catch (nsm: NoSuchMethodError) {
                                ApiResult.NotSupported
                            }
                        ${if (defaultBlocking) "}" else ""}
                    """.trimIndent())
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("executeDisabled")
                            .addModifiers(KModifier.OVERRIDE, KModifier.SUSPEND)
                            .addParameter("params", paramType)
                            .addParameter("state", ClassName("kotlin", "Boolean"))
                            .returns(
                                ClassName("net.sfelabs.core.domain.api", "ApiResult")
                                    .parameterizedBy(returnType)
                            )
                            .addCode("""
                        return ApiResult.Error(UiText.DynamicString("Feature is disabled"))
                    """.trimIndent())
                            .build()
                    )
                    .build()
            )
            .build()

        fileSpec.writeTo(codeGenerator, Dependencies(true, classDecl.containingFile!!))
    }

    private fun generateStandardImplementation(
        classDecl: KSClassDeclaration,
        defaultBlocking: Boolean,
        paramType: TypeName,
        returnType: TypeName
    ) {
        val fileSpec = FileSpec.builder(
            classDecl.packageName.asString(),
            "${classDecl.simpleName.asString()}Generated"
        )
            .addType(
                TypeSpec.classBuilder("${classDecl.simpleName.asString()}Generated")
                    .superclass(classDecl.asType(emptyList()).toTypeName())
                    .addType(generateBuilderClass(classDecl, paramType, returnType))
                    .addFunction(
                        FunSpec.builder("executeEnabled")
                            .addModifiers(KModifier.OVERRIDE, KModifier.SUSPEND)
                            .addParameter("params", paramType)
                            .addParameter("state", ClassName("kotlin", "Boolean"))
                            .returns(
                                ClassName("net.sfelabs.core.domain.api", "ApiResult")
                                    .parameterizedBy(returnType)
                            )
                            .addCode("""
                        ${if (defaultBlocking) "return withContext(dispatcher ?: defaultDispatcher) {" else "return"}
                            try {
                                ApiResult.Success(Unit as $returnType)
                            } catch (e: Exception) {
                                mapError(e)
                            }
                        ${if (defaultBlocking) "}" else ""}
                    """.trimIndent())
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("executeDisabled")
                            .addModifiers(KModifier.OVERRIDE, KModifier.SUSPEND)
                            .addParameter("params", paramType)
                            .addParameter("state", ClassName("kotlin", "Boolean"))
                            .returns(
                                ClassName("net.sfelabs.core.domain.api", "ApiResult")
                                    .parameterizedBy(returnType)
                            )
                            .addCode("""
                        return ApiResult.Error(UiText.DynamicString("Feature is disabled"))
                    """.trimIndent())
                            .build()
                    )
                    .build()
            )
            .build()

        fileSpec.writeTo(codeGenerator, Dependencies(true, classDecl.containingFile!!))
    }


    private fun generateBuilderClass(
        classDecl: KSClassDeclaration,
        paramType: TypeName,
        returnType: TypeName
    ): TypeSpec {
        return TypeSpec.classBuilder("Builder")
            .superclass(
                ClassName("net.sfelabs.core.knoxfeature.domain.usecase.builder", "FeatureUseCaseBuilder")
                    .parameterizedBy(paramType, returnType)
            )
            .addFunction(
                FunSpec.builder("build")
                    .addModifiers(KModifier.OVERRIDE)
                    .returns(
                        ClassName("net.sfelabs.core.knoxfeature.domain.usecase.base", "FeatureUseCase")
                            .parameterizedBy(paramType, returnType)
                    )
                    .addCode("""
                    requireNotNull(api) { "API must be provided" }
                    return ${classDecl.simpleName.asString()}Generated(
                        api as ${classDecl.asType(emptyList()).toTypeName()},
                        dispatcher
                    )
                """.trimIndent())
                    .build()
            )
            .build()
    }

    private fun getFeatureUseCaseTypes(featureUseCaseType: KSTypeReference): Triple<TypeName, TypeName, TypeName> {
        val args = featureUseCaseType.element?.typeArguments ?: emptyList()
        val featureType = args[0].type?.resolve()?.toTypeName() ?: STAR
        val paramType = args[1].type?.resolve()?.toTypeName() ?: STAR
        val returnType = args[2].type?.resolve()?.toTypeName() ?: STAR
        return Triple(featureType, paramType, returnType)
    }
}
