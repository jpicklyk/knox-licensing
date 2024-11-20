package net.sfelabs.core.knoxfeature

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.sfelabs.core.knoxfeature.domain.model.old.KnoxFeature
import java.io.InputStream

private val mapper = jacksonObjectMapper()

fun processJson(jsonDataString: String): KnoxFeature {
    return mapper.readValue<KnoxFeature>(jsonDataString)
}

fun provideJson(feature: KnoxFeature): String {
    return mapper.writeValueAsString(feature)
}

fun processFeatureList(jsonDataString: String): List<KnoxFeature> {
    return mapper.readValue<List<KnoxFeature>>(jsonDataString)
}

fun processFeatureList(jsonDataStream: InputStream): List<KnoxFeature> {
    mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    return mapper.readValue<List<KnoxFeature>>(jsonDataStream)
}