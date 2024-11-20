package net.sfelabs.core.knoxfeature.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.*
import net.sfelabs.core.domain.use_case.ApiUseCase
import net.sfelabs.core.domain.use_case.CoroutineApiUseCase
import net.sfelabs.core.knoxfeature.domain.metrics.UseCaseMetrics

class GeneratedUseCaseProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) {
    fun generateUseCase(classDecl: KSClassDeclaration) {
        val packageName = classDecl.packageName.asString()
        val className = "${classDecl.simpleName.asString()}Generated"

        // Get annotation details
        val annotation = classDecl.annotations.first {
            it.shortName.asString() == "GeneratedUseCase"
        }
        val name = annotation.arguments.first { it.name?.asString() == "name" }.value as String
        val withMetrics = annotation.arguments.first { it.name?.asString() == "withMetrics" }.value as Boolean
        val defaultBlocking = annotation.arguments.first { it.name?.asString() == "defaultBlocking" }.value as Boolean

        // Find input/output types from base class
        val apiUseCaseType = classDecl.superTypes.first {
            it.resolve().declaration.qualifiedName?.asString()?.contains("ApiUseCase") == true
        }
        val (paramType, returnType) = getUseCaseTypes(apiUseCaseType)

        // Generate class using KotlinPoet
        val file = FileSpec.builder(packageName, className)
            .addType(
                TypeSpec.classBuilder(className)
                    .superclass(
                        if (defaultBlocking) {
                            CoroutineApiUseCase::class.asClassName()
                                .parameterizedBy(paramType, returnType)
                        } else {
                            ApiUseCase::class.asClassName()
                                .parameterizedBy(paramType, returnType)
                        }
                    )
                    .primaryConstructor(
                        FunSpec.constructorBuilder()
                            .addParameter("api", classDecl.asType(emptyList()).toTypeName())
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder("api", classDecl.asType(emptyList()).toTypeName())
                            .initializer("api")
                            .addModifiers(KModifier.PRIVATE)
                            .build()
                    )
                    .apply {
                        if (withMetrics) {
                            addProperty(
                                PropertySpec.builder(
                                    "metrics",
                                    UseCaseMetrics::class,
                                    KModifier.PRIVATE
                                )
                                    .initializer("%T()", UseCaseMetrics::class)
                                    .build()
                            )
                        }
                    }
                    .addFunction(generateExecuteFunction(
                        paramType,
                        returnType,
                        withMetrics,
                        defaultBlocking
                    ))
                    .build()
            )
            .build()

        // Write to file
        file.writeTo(
            codeGenerator,
            Dependencies(true, classDecl.containingFile!!)
        )
    }

    private fun generateExecuteFunction(
        paramType: TypeName,
        returnType: TypeName,
        withMetrics: Boolean,
        isBlocking: Boolean
    ): FunSpec {
        return FunSpec.builder("execute")
            .addModifiers(KModifier.OVERRIDE, KModifier.SUSPEND)
            .addParameter("params", paramType)
            .returns(
                net.sfelabs.core.domain.api.ApiResult::class.asClassName()
                    .parameterizedBy(returnType)
            )
            .addCode(buildCodeBlock {
                if (withMetrics) {
                    addStatement("val startTime = System.currentTimeMillis()")
                }

                beginControlFlow("return try")
                if (isBlocking) {
                    addStatement("withContext(dispatcher ?: defaultDispatcher) {")
                }
                addStatement("val result = api.execute(params)")
                if (withMetrics) {
                    addStatement("metrics.recordSuccess(this::class.simpleName ?: \"Unknown\")")
                }
                addStatement("result")
                if (isBlocking) {
                    addStatement("}")
                }

                nextControlFlow("catch (e: Exception)")
                if (withMetrics) {
                    addStatement("metrics.recordError(this::class.simpleName ?: \"Unknown\")")
                }
                addStatement("mapError(e)")

                if (withMetrics) {
                    nextControlFlow("finally")
                    addStatement("""
                        metrics.recordDuration(
                            this::class.simpleName ?: "Unknown",
                            System.currentTimeMillis() - startTime
                        )
                    """.trimIndent())
                }

                endControlFlow()
            })
            .build()
    }

    private fun getUseCaseTypes(apiUseCaseType: KSTypeReference): Pair<TypeName, TypeName> {
        val args = apiUseCaseType.element?.typeArguments ?: emptyList()
        val paramType = args[0].type?.resolve()?.toTypeName() ?: STAR
        val returnType = args[1].type?.resolve()?.toTypeName() ?: STAR
        return paramType to returnType
    }
}