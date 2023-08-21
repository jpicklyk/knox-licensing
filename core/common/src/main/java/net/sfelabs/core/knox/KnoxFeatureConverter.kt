package net.sfelabs.core.knox

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

private val mapper = jacksonObjectMapper()

fun processJson(jsonDataString: String): KnoxFeature {
    return mapper.readValue<KnoxFeature>(jsonDataString)
}

fun provideJson(feature: KnoxFeature): String {
    return mapper.writeValueAsString(feature)
}