package net.sfelabs.common.core.signing

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.os.Build
import java.security.MessageDigest
import kotlin.jvm.Throws

@Throws(NameNotFoundException::class)
fun getApplicationSignatures(packageName: String, context: Context): List<String> {
    val signatureList: List<String>
    val signingInfo =
    getPackageInfo(
            packageName,
            context,
            PackageManager.GET_SIGNING_CERTIFICATES
        ).signingInfo
    signatureList = if(signingInfo.hasMultipleSigners()) {
        signingInfo.apkContentsSigners.map {
            it.toCharsString()
        }
    } else {
        signingInfo.signingCertificateHistory.map {
            it.toCharsString()
        }
    }
    return signatureList
}

@Throws(NameNotFoundException::class)
fun getApplicationSignatureThumbprints(packageName: String, context: Context): List<String> {
    val signatureList: List<String>
    val signingInfo =
        getPackageInfo(
            packageName,
            context,
            PackageManager.GET_SIGNING_CERTIFICATES
        ).signingInfo
    signatureList = if(signingInfo.hasMultipleSigners()) {
        signingInfo.apkContentsSigners.map {
            val sha = MessageDigest.getInstance("SHA")
            sha.update(it.toByteArray())
            sha.digest().toHex()
        }
    } else {
        signingInfo.signingCertificateHistory.map {
            val sha = MessageDigest.getInstance("SHA")
            sha.update(it.toByteArray())
            sha.digest().toHex()
            it.toCharsString()
        }
    }
    return signatureList
}

@Throws(NameNotFoundException::class)
fun getPackageInfo(
    packageName: String,
    context: Context,
    flag: Int
): PackageInfo {
    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.packageManager.getPackageInfo(
            packageName,
            PackageManager.PackageInfoFlags.of(flag.toLong())
        )
    } else {
        context.packageManager.getPackageInfo(packageName, flag)
    }
}

fun isPackageInstalled(packageName: String, context: Context): Boolean {
    return try {
        getPackageInfo(packageName, context, PackageManager.GET_ACTIVITIES)
        true
    } catch (e: NameNotFoundException) {
        false
    }
}

fun isPackageOnDevice(packageName: String, context: Context): Boolean {
    try {
        getPackageInfo(packageName, context, PackageManager.GET_META_DATA)
    } catch (e: NameNotFoundException) {
        return false
    }
    return true
}


fun ByteArray.toHex(): String = joinToString(separator = "") {
    eachByte -> "%02x".format(eachByte)
}
