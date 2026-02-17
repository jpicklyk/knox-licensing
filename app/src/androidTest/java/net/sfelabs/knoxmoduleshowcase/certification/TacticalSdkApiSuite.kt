package net.sfelabs.knoxmoduleshowcase.certification

import net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk.ApiExistenceV100Tests
import net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk.ApiExistenceV110Tests
import net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk.ApiExistenceV112Tests
import net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk.ApiExistenceV130Tests
import net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk.ApiExistenceV131Tests
import net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk.ApiExistenceV132Tests
import net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk.ApiExistenceV134Tests
import net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk.ApiExistenceV140Tests
import net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk.ApiExistenceV141Tests
import net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk.CheckLinuxConfigurations
import net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk.CheckSpecialFileAccess
import net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk.CheckTacticalDefaults
import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * Knox Tactical SDK API Existence Verification Suite
 *
 * Verifies that all required Knox Tactical SDK APIs are present on the device firmware.
 * These tests use reflection to confirm that expected methods exist on Knox SDK manager
 * classes, ensuring firmware compatibility before running functional tests.
 *
 * Tests are organized by the firmware release version in which each API was introduced.
 * To add a new API existence check, find the appropriate ApiExistenceVXXX file for the
 * release version in which the API was added, and add a test method there. When introducing
 * APIs for a new release version, create a new ApiExistenceVXXX file and add it here.
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
    // Knox Tactical SDK API existence checks — organized by release version
    ApiExistenceV100Tests::class,
    ApiExistenceV110Tests::class,
    ApiExistenceV112Tests::class,
    ApiExistenceV130Tests::class,
    ApiExistenceV131Tests::class,
    ApiExistenceV132Tests::class,
    ApiExistenceV134Tests::class,
    ApiExistenceV140Tests::class,
    ApiExistenceV141Tests::class,

    // Linux kernel and device configuration
    CheckLinuxConfigurations::class,
    CheckSpecialFileAccess::class,
    CheckTacticalDefaults::class,
)
class TacticalSdkApiSuite
