package net.sfelabs.knoxmoduleshowcase.certification

import net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk.AdbTests
import net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk.ChargingTests
import net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk.CheckLinuxConfigurations
import net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk.CheckSpecialFileAccess
import net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk.CheckTacticalDefaults
import net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk.EthernetTests
import net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk.HdmTests
import net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk.IpsecTests
import net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk.NatTests
import net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk.PhoneTests
import net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk.PogoTests
import net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk.RadioTests
import net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk.RamPlusTest
import net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk.ScreenTests
import net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk.TcpTests
import net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk.UsbTests
import net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk.WiFiTests
import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * Knox Tactical SDK API Existence Verification Suite
 *
 * Verifies that all required Knox Tactical SDK APIs are present on the device firmware.
 * These tests use reflection to confirm that expected methods exist on Knox SDK manager
 * classes, ensuring firmware compatibility before running functional tests.
 *
 * Also validates Linux kernel configurations (PPP drivers, USB network drivers, CDC support),
 * special file access (sysfs, ueventd, SELinux contexts), and tactical default settings.
 *
 * This suite is included as a nested suite within [UsbAdbCertificationSuite] and can also
 * be run independently:
 * ```
 * ./gradlew connectedAndroidTest \
 *   -Pandroid.testInstrumentationRunnerArguments.class=net.sfelabs.knoxmoduleshowcase.certification.TacticalSdkApiSuite
 * ```
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
    // API existence checks
    AdbTests::class,
    ChargingTests::class,
    EthernetTests::class,
    HdmTests::class,
    IpsecTests::class,
    NatTests::class,
    PhoneTests::class,
    PogoTests::class,
    RadioTests::class,
    RamPlusTest::class,
    ScreenTests::class,
    TcpTests::class,
    UsbTests::class,
    WiFiTests::class,

    // Linux kernel and device configuration
    CheckLinuxConfigurations::class,
    CheckSpecialFileAccess::class,
    CheckTacticalDefaults::class,
)
class TacticalSdkApiSuite
