package net.sfelabs.knoxmoduleshowcase.certification

/**
 * Full Device Certification
 *
 * Complete device certification requires running both the [UsbAdbCertificationSuite] and
 * [WifiAdbCertificationSuite] sequentially, since they require different ADB transport
 * connections that cannot be active simultaneously.
 *
 * ## Certification Procedure
 *
 * ### Phase 1: USB ADB Certification
 * 1. Connect the device via USB cable
 * 2. Enable USB debugging in Developer Options
 * 3. Insert SIM card with active carrier data plan
 * 4. Connect ethernet adapter
 * 5. Activate Knox license and enable device owner
 * 6. Run:
 * ```
 * ./gradlew connectedAndroidTest \
 *   -Pandroid.testInstrumentationRunnerArguments.class=net.sfelabs.knoxmoduleshowcase.certification.UsbAdbCertificationSuite
 * ```
 *
 * ### Phase 2: WiFi ADB Certification
 * 1. Enable Wireless debugging in Developer Options
 * 2. Pair and connect via WiFi ADB: `adb pair <ip>:<port>`, then `adb connect <ip>:<port>`
 * 3. Disable USB debugging in Developer Options
 * 4. Disconnect the USB cable
 * 5. Ensure ethernet adapter remains connected
 * 6. Run:
 * ```
 * ./gradlew connectedAndroidTest \
 *   -Pandroid.testInstrumentationRunnerArguments.class=net.sfelabs.knoxmoduleshowcase.certification.WifiAdbCertificationSuite
 * ```
 *
 * ### Phase 3: Review Results
 * Combine the test results from both phases to produce the full certification report.
 * Test results are located at:
 * ```
 * app/build/reports/androidTests/connected/
 * ```
 *
 * ## Coverage Summary
 * - **USB ADB Suite**: 58 test classes covering radio, screen, charging, USB, WiFi, HDM,
 *   Knox core, phone, audit, applications, ethernet (NAT/MAC), IPsec, TCP, and more.
 *   Includes 13 Knox Tactical SDK API verification tests (API existence, Linux kernel
 *   configurations, device defaults). HdmTests WiFi-required methods are automatically skipped.
 * - **WiFi ADB Suite**: 5 test classes covering ADB WiFi routing, ethernet IP/VLAN
 *   configuration, HDM USB hardware controls, and Linux tunneling.
 *   HdmTests USB-required methods are automatically skipped.
 *
 * ## Hardware Requirements
 * - Samsung device with Knox Tactical SDK support
 * - USB cable (Phase 1)
 * - WiFi network (Phase 2)
 * - Ethernet adapter (USB-C or USB-A)
 * - SIM card with active carrier data plan
 */
class FullCertificationSuite
