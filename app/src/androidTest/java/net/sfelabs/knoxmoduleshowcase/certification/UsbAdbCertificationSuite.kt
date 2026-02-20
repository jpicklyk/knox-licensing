package net.sfelabs.knoxmoduleshowcase.certification

import net.sfelabs.knoxmoduleshowcase.tests.adb.ExecuteAdbCommandTest
import net.sfelabs.knoxmoduleshowcase.tests.adb.ExecuteAndGetAdbCommandTest
import net.sfelabs.knoxmoduleshowcase.tests.applications.ApplicationTests
import net.sfelabs.knoxmoduleshowcase.tests.audit.AuditLoggingTests
import net.sfelabs.knoxmoduleshowcase.tests.bridging.BridgeTests
import net.sfelabs.knoxmoduleshowcase.tests.charging.ChargingTests
import net.sfelabs.knoxmoduleshowcase.tests.charging.WirelessChargingTest
import net.sfelabs.knoxmoduleshowcase.tests.hdm.HdmTests
import net.sfelabs.knoxmoduleshowcase.tests.hdm.SupportedComponents
import net.sfelabs.knoxmoduleshowcase.tests.ipsec.IpsecXfrmCommandTest
import net.sfelabs.knoxmoduleshowcase.tests.knox_core.AllowOtaUpgradeTests
import net.sfelabs.knoxmoduleshowcase.tests.knox_core.AttestationTests
import net.sfelabs.knoxmoduleshowcase.tests.knox_core.CheckKnoxPermissionsGranted
import net.sfelabs.knoxmoduleshowcase.tests.knox_core.FirmwareRecoveryTests
import net.sfelabs.knoxmoduleshowcase.tests.phone.AutoCallPickupTests
import net.sfelabs.knoxmoduleshowcase.tests.phone.AutoRecordCallTest
import net.sfelabs.knoxmoduleshowcase.tests.pogo.PogoTests
import net.sfelabs.knoxmoduleshowcase.tests.radio.AlwaysRadioOnTests
import net.sfelabs.knoxmoduleshowcase.tests.radio.BandLocking5gTests
import net.sfelabs.knoxmoduleshowcase.tests.radio.E911Tests
import net.sfelabs.knoxmoduleshowcase.tests.radio.BandLockingLteTests
import net.sfelabs.knoxmoduleshowcase.tests.radio.ElectronicSimTests
import net.sfelabs.knoxmoduleshowcase.tests.radio.ImsTests
import net.sfelabs.knoxmoduleshowcase.tests.radio.Nr5GModeTests
import net.sfelabs.knoxmoduleshowcase.tests.radio.PLMNAllowedListTests
import net.sfelabs.knoxmoduleshowcase.tests.radio.Radio2gConnectivityTests
import net.sfelabs.knoxmoduleshowcase.tests.radio.TacticalDeviceModeTest
import net.sfelabs.knoxmoduleshowcase.tests.screen.AutoTouchSensitivityTest
import net.sfelabs.knoxmoduleshowcase.tests.screen.LcdBacklightStateTest
import net.sfelabs.knoxmoduleshowcase.tests.screen.LockscreenTests
import net.sfelabs.knoxmoduleshowcase.tests.screen.NightVisionModeTest
import net.sfelabs.knoxmoduleshowcase.tests.screen.ScreenBrightnessTests
import net.sfelabs.knoxmoduleshowcase.tests.tcp.TcpDumpTests
import net.sfelabs.knoxmoduleshowcase.tests.tsapp.TacticalSettingsAppTests
import net.sfelabs.knoxmoduleshowcase.tests.usb.LargeUsbWhitelistTests
import net.sfelabs.knoxmoduleshowcase.tests.usb.UsbDeviceAccessTests
import net.sfelabs.knoxmoduleshowcase.tests.usb.UsbDeviceAccessVidPidTests
import net.sfelabs.knoxmoduleshowcase.tests.usb.UsbHostWhiteListTest
import net.sfelabs.knoxmoduleshowcase.tests.wifi.HotspotTest
import net.sfelabs.knoxmoduleshowcase.tests.wifi.RandomizedMacAddressTests
import net.sfelabs.knoxmoduleshowcase.tests.wifi.Wlan0MtuTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * USB ADB Device Certification Suite
 *
 * Validates all Knox features accessible over a USB ADB connection. This is the primary
 * certification suite and covers the majority of tests. Run this suite first during device
 * certification.
 *
 * Tests from [HdmTests] that are annotated with [@AdbWifiRequired][net.sfelabs.knox.core.testing.rules.AdbWifiRequired]
 * will be automatically skipped when running over USB ADB.
 *
 * ## Prerequisites
 * - Device connected via **USB ADB cable**
 * - USB debugging **enabled** in Developer Options
 * - **SIM card** inserted with active carrier data plan
 * - **Knox license** activated
 * - **Device owner** (DPC) enabled via `adb shell dpm set-device-owner`
 * - **SIM card** inserted with active carrier data plan (for radio tests)
 *
 * ## Running
 * ```
 * ./gradlew connectedAndroidTest \
 *   -Pandroid.testInstrumentationRunnerArguments.class=net.sfelabs.knoxmoduleshowcase.certification.UsbAdbCertificationSuite
 * ```
 *
 * ## Test Categories
 * - **ADB**: ExecuteAdbCommand
 * - **Applications**: Application management
 * - **Audit**: Audit logging
 * - **Bridging**: Network bridge configuration
 * - **Charging**: Wired and wireless charging controls
 * - **HDM**: Hardware Device Manager (camera, bluetooth, GPS, NFC, microphone, modem, speaker, MMC)
 * - **IPsec**: XFRM tunneling commands
 * - **Knox Core**: Attestation, CC Mode, firmware, OTA, permissions
 * - **Phone**: Auto call pickup, auto call recording
 * - **Pogo**: Pogo pin configuration
 * - **Radio**: Band locking (LTE/5G), 2G connectivity, always-on radio, eSIM, IMS, mobile data, NR5G, PLMN, tactical device mode
 * - **Screen**: Brightness, lockscreen, LCD backlight, night vision, touch sensitivity
 * - **TCP**: Packet capture
 * - **Tactical Settings**: Tactical settings app
 * - **USB**: Device access, VID/PID filtering, host whitelist, large whitelist
 * - **WiFi**: Hotspot, randomized MAC, wlan0 MTU
 *
 * ### Knox Tactical SDK API Verification (tactical_sdk)
 * Verifies that required Knox Tactical SDK APIs exist on the device firmware:
 * - **ADB, Ethernet, HDM, Phone, Radio, RAM, Screen, TCP, USB, WiFi**: API existence checks
 * - **Linux**: Kernel configurations (PPP, USB drivers, CDC), special file access (sysfs, ueventd, SELinux)
 * - **Configuration**: Tactical default settings (RAM Plus, airplane mode)
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
    // Knox Tactical SDK API Verification (nested suite)
    TacticalSdkApiSuite::class,

    // ADB
    ExecuteAdbCommandTest::class,
    ExecuteAndGetAdbCommandTest::class,

    // Android / KeyStore

    // Applications
    ApplicationTests::class,

    // Audit
    AuditLoggingTests::class,

    // Bridging
    BridgeTests::class,

    // Charging
    ChargingTests::class,
    WirelessChargingTest::class,

    // HDM (Hardware Device Manager)
    HdmTests::class,
    SupportedComponents::class,

    // IPsec
    IpsecXfrmCommandTest::class,

    // Knox Core
    AllowOtaUpgradeTests::class,
    AttestationTests::class,
    CheckKnoxPermissionsGranted::class,
    FirmwareRecoveryTests::class,

    // Phone
    AutoCallPickupTests::class,
    AutoRecordCallTest::class,

    // Pogo
    PogoTests::class,

    // Radio
    AlwaysRadioOnTests::class,
    BandLocking5gTests::class,
    BandLockingLteTests::class,
    E911Tests::class,
    ElectronicSimTests::class,
    ImsTests::class,
    Nr5GModeTests::class,
    PLMNAllowedListTests::class,
    Radio2gConnectivityTests::class,
    TacticalDeviceModeTest::class,

    // Screen
    AutoTouchSensitivityTest::class,
    LcdBacklightStateTest::class,
    LockscreenTests::class,
    NightVisionModeTest::class,
    ScreenBrightnessTests::class,

    // TCP
    TcpDumpTests::class,

    // Tactical Settings
    TacticalSettingsAppTests::class,

    // USB
    LargeUsbWhitelistTests::class,
    UsbDeviceAccessTests::class,
    UsbDeviceAccessVidPidTests::class,
    UsbHostWhiteListTest::class,

    // WiFi
    HotspotTest::class,
    RandomizedMacAddressTests::class,
    Wlan0MtuTest::class,

)
class UsbAdbCertificationSuite
