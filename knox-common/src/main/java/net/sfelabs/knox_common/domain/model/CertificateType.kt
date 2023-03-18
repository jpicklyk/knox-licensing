package net.sfelabs.knox_common.domain.model

sealed class CertificateType(val type: String) {
    object Cert: CertificateType("CERT")
    object Pkcs12: CertificateType("PKCS12")
}
