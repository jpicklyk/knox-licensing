package net.sfelabs.knox_common.domain.use_cases.attestation

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.os.Build
import android.security.AttestedKeyPair
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.UiText
import java.math.BigInteger
import java.security.SecureRandom
import java.security.spec.ECGenParameterSpec
import java.util.Date
import javax.inject.Inject
import javax.security.auth.x500.X500Principal

class KeyGeneratorUseCase @Inject constructor(
    private val devicePolicyManager: DevicePolicyManager
){

    @RequiresApi(Build.VERSION_CODES.S)
    suspend operator fun invoke(): ApiResult<Unit> {
        return coroutineScope {
            try {
                val result = generateKey()
                if (result == null)
                    ApiResult.Error(UiText.DynamicString("Error, generate keypair returned null"))
                else {
                    println("Key generated: ${result.keyPair}")
                    ApiResult.Success(Unit)
                }
            } catch (e: Exception) {
                ApiResult.Error(UiText.DynamicString(e.toString()))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun generateKey(): AttestedKeyPair? {
        val builder: KeyGenParameterSpec.Builder =
            KeyGenParameterSpec.Builder("alias", KeyProperties.PURPOSE_SIGN)
        val challenge = ByteArray(16)
        SecureRandom().nextBytes(challenge)
        builder.setAttestationChallenge(challenge)
        builder.setCertificateSerialNumber(BigInteger.valueOf(42))
        builder.setCertificateSubject(X500Principal("C=US, ST=Texas, O=ABC, OU=XYZ, CN=ABCDEFGH12345678"))


        val startTime: Long = System.currentTimeMillis()
        builder.setCertificateNotBefore(Date(startTime))
        builder.setCertificateNotAfter(Date(startTime + 365L * 24L * 60L * 60L * 1000L - 1L))
        builder.setUserAuthenticationRequired(false)
        builder.setIsStrongBoxBacked(true)
        builder.setUnlockedDeviceRequired(false)
        builder.setDevicePropertiesAttestationIncluded(true)
        builder.setDigests(KeyProperties.DIGEST_SHA256)
        //builder.setSignaturePaddings(arrayOf<String>(0))
        //builder . setEncryptionPaddings arrayOfNulls<String>(0)
        builder.setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
        val name = ComponentName(
            "net.sfelabs.knoxmoduleshowcase",
            "net.sfelabs.knoxmoduleshowcase.app.receivers.AdminReceiver"
        )
        return devicePolicyManager.generateKeyPair(
            name,
            "EC",
            builder.build(),
            DevicePolicyManager.ID_TYPE_BASE_INFO or DevicePolicyManager.ID_TYPE_SERIAL
        )
    }

}