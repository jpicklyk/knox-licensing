package net.sfelabs.knoxmoduleshowcase.app

import android.telephony.TelephonyManager

fun decodeAllowedNetworkTypes(value: Long): List<String> {
    val networkTypes = mutableListOf<String>()
    if ((value and TelephonyManager.NETWORK_TYPE_BITMASK_GSM) != 0L) {
        networkTypes.add("GSM")
    }

    if ((value and TelephonyManager.NETWORK_TYPE_BITMASK_GPRS) != 0L) {
        networkTypes.add("GPRS")
    }

    if ((value and TelephonyManager.NETWORK_TYPE_BITMASK_EDGE) != 0L) {
        networkTypes.add("EDGE")
    }

    if ((value and TelephonyManager.NETWORK_TYPE_BITMASK_CDMA) != 0L) {
        networkTypes.add("CDMA")
    }

    if ((value and TelephonyManager.NETWORK_TYPE_BITMASK_1xRTT) != 0L) {
        networkTypes.add("1xRTT")
    }

    if ((value and TelephonyManager.NETWORK_TYPE_BITMASK_EVDO_0) != 0L) {
        networkTypes.add("EVDO 0")
    }

    if ((value and TelephonyManager.NETWORK_TYPE_BITMASK_EVDO_A) != 0L) {
        networkTypes.add("EVDO A")
    }

    if ((value and TelephonyManager.NETWORK_TYPE_BITMASK_EVDO_B) != 0L) {
        networkTypes.add("EVDO B")
    }

    if ((value and TelephonyManager.NETWORK_TYPE_BITMASK_EHRPD) != 0L) {
        networkTypes.add("EHRPD")
    }

    if ((value and TelephonyManager.NETWORK_TYPE_BITMASK_HSUPA) != 0L) {
        networkTypes.add("HSUPA")
    }

    if ((value and TelephonyManager.NETWORK_TYPE_BITMASK_HSDPA) != 0L) {
        networkTypes.add("HSDPA")
    }

    if ((value and TelephonyManager.NETWORK_TYPE_BITMASK_HSPA) != 0L) {
        networkTypes.add("HSPA")
    }

    if ((value and TelephonyManager.NETWORK_TYPE_BITMASK_HSPAP) != 0L) {
        networkTypes.add("HSPAP")
    }

    if ((value and TelephonyManager.NETWORK_TYPE_BITMASK_UMTS) != 0L) {
        networkTypes.add("UMTS")
    }

    if ((value and TelephonyManager.NETWORK_TYPE_BITMASK_TD_SCDMA) != 0L) {
        networkTypes.add("TD-SCDMA")
    }

    if ((value and TelephonyManager.NETWORK_TYPE_BITMASK_LTE) != 0L) {
        networkTypes.add("LTE")
    }

    if ((value and TelephonyManager.NETWORK_TYPE_BITMASK_LTE_CA) != 0L) {
        networkTypes.add("LTE CA")
    }

    if ((value and TelephonyManager.NETWORK_TYPE_BITMASK_NR) != 0L) {
        networkTypes.add("NR")
    }

    if ((value and TelephonyManager.NETWORK_TYPE_BITMASK_IWLAN) != 0L) {
        networkTypes.add("IWLAN")
    }

    return networkTypes
}