package net.sfelabs.knoxmoduleshowcase

import org.junit.platform.runner.JUnitPlatform
import org.junit.platform.suite.api.IncludePackages
import org.junit.platform.suite.api.SelectPackages
import org.junit.runner.RunWith

@Suppress("DEPRECATION")
@RunWith(JUnitPlatform::class)
//@SuiteDisplayName("Tactical Edition Certification Suite")
@SelectPackages("net.sfelabs.knoxmoduleshowcase")
@IncludePackages("net.sfelabs.knoxmoduleshowcase.tests.applications")
class TacticalCertificationSuite