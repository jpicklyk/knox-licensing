package net.sfelabs.knoxmoduleshowcase.certification

import net.sfelabs.knoxmoduleshowcase.tests.adb.GuardAdbOverWlanTests
import net.sfelabs.knoxmoduleshowcase.tests.ethernet.AddIpAddressToInterfaceTests
import net.sfelabs.knoxmoduleshowcase.tests.ethernet.GetMacAddressViaAidlHiltExampleTest
import net.sfelabs.knoxmoduleshowcase.tests.ethernet.GetMacAddressViaAidlIntegrationTest
import net.sfelabs.knoxmoduleshowcase.tests.ethernet.MultiEthernetConfigurationTest
import net.sfelabs.knoxmoduleshowcase.tests.ethernet.VlanTests
import net.sfelabs.knoxmoduleshowcase.tests.hdm.HdmTests
import net.sfelabs.knoxmoduleshowcase.tests.tunneling.LinuxTunnelingTests
import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * WiFi ADB Device Certification Suite
 *
 * Validates Knox features that require a WiFi ADB connection. These tests either manipulate
 * USB hardware (which would disconnect USB ADB) or configure ethernet/network interfaces
 * that benefit from a non-USB ADB transport.
 *
 * Run this suite **after** completing [UsbAdbCertificationSuite] to achieve full device
 * certification coverage.
 *
 * Tests from [HdmTests] that are annotated with [@AdbUsbRequired][net.sfelabs.knox.core.testing.rules.AdbUsbRequired]
 * will be automatically skipped when running over WiFi ADB.
 *
 * ## Prerequisites
 * - Device connected via **WiFi ADB** (wireless debugging)
 * - USB debugging **disabled** in Developer Options
 * - USB cable **disconnected** (to ensure USB debugging stays disabled)
 * - **Ethernet adapter** connected via USB-C/USB-A
 * - **Knox license** activated
 * - **Device owner** (DPC) enabled via `adb shell dpm set-device-owner`
 *
 * ## WiFi ADB Setup
 * 1. Enable **Wireless debugging** in Developer Options
 * 2. Pair the device: `adb pair <ip>:<port>` (enter the pairing code)
 * 3. Connect: `adb connect <ip>:<port>`
 * 4. Disable **USB debugging** in Developer Options
 * 5. Disconnect the USB cable
 * 6. Verify: `adb devices` should show the WiFi connection only
 *
 * ## Running
 * ```
 * ./gradlew connectedAndroidTest \
 *   -Pandroid.testInstrumentationRunnerArguments.class=net.sfelabs.knoxmoduleshowcase.certification.WifiAdbCertificationSuite
 * ```
 *
 * ## Test Categories
 * - **ADB**: WiFi ADB routing guard (ensures ADB traffic stays on wlan0)
 * - **Ethernet**: MAC address (AIDL), multi-ethernet configuration, IP address assignment, VLAN configuration
 * - **HDM**: USB hardware disable/enable (requires WiFi ADB to maintain connection)
 * - **Tunneling**: Linux tunnel interface creation (GRE, IPIP, SIT)
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
    // ADB
    GuardAdbOverWlanTests::class,

    // Ethernet
    AddIpAddressToInterfaceTests::class,
    GetMacAddressViaAidlHiltExampleTest::class,
    GetMacAddressViaAidlIntegrationTest::class,
    MultiEthernetConfigurationTest::class,
    VlanTests::class,

    // HDM (WiFi-required tests: USB disable/enable)
    HdmTests::class,

    // Tunneling
    LinuxTunnelingTests::class,
)
class WifiAdbCertificationSuite
