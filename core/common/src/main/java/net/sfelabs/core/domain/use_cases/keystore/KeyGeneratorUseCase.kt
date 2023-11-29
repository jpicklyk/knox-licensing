package net.sfelabs.core.domain.use_cases.keystore

import android.app.admin.DevicePolicyManager
import android.os.Build
import android.security.AttestedKeyPair
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.KeyProperties.PURPOSE_SIGN
import androidx.annotation.RequiresApi
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.ApiCall
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
    suspend operator fun invoke(): ApiCall<Unit> {
        return coroutineScope {
            try {
                val result = generateKey()
                if(result == null)
                    ApiCall.Error(UiText.DynamicString("Error, generate keypair returned null"))
                else
                    ApiCall.Success(Unit)
            } catch (e: Exception) {
                ApiCall.Error(UiText.DynamicString(e.toString()))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun generateKey(): AttestedKeyPair? {
        val builder: KeyGenParameterSpec.Builder =
            KeyGenParameterSpec.Builder("alias", PURPOSE_SIGN)
        val challenge = ByteArray(16)
        SecureRandom().nextBytes(challenge)
        builder.setAttestationChallenge(challenge)
        builder.setCertificateSerialNumber(BigInteger.valueOf(42))
        builder.setCertificateSubject(X500Principal("C=US, ST=California, L=Carlsbad, O=Viasat, OU=MDD, CN=R5CW7189GEY"))


        val startTime: Long = System.currentTimeMillis()
        builder.setCertificateNotBefore(Date(startTime))
        builder.setCertificateNotAfter(Date(startTime + 365L * 24L * 60L * 60L * 1000L - 1L))
        builder.setUserAuthenticationRequired(false)
        builder.setIsStrongBoxBacked(false)
        builder.setUnlockedDeviceRequired(false)
        builder.setDevicePropertiesAttestationIncluded(true)
        builder.setDigests(KeyProperties.DIGEST_SHA256)
        //builder.setSignaturePaddings(arrayOf<String>(0))
        //builder . setEncryptionPaddings arrayOfNulls<String>(0)
        builder.setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
        val name = devicePolicyManager.activeAdmins!![1]
        return devicePolicyManager.generateKeyPair(
            name,
            "EC",
            builder.build(),
            DevicePolicyManager.ID_TYPE_BASE_INFO or DevicePolicyManager.ID_TYPE_IMEI or DevicePolicyManager.ID_TYPE_SERIAL
        )
    }

}