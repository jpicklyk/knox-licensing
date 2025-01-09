package net.sfelabs.knoxmoduleshowcase.tests.knox_core

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.samsung.android.knox.custom.CustomDeviceManager
import junit.framework.TestCase.assertTrue
import net.sfelabs.knox_tactical.di.KnoxModule
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class CheckKnoxPermissionsGranted {
    private lateinit var cdm: CustomDeviceManager

    @Before
    fun setup() {
        cdm = KnoxModule.provideKnoxCustomDeviceManager()
    }

    @Test
    fun checkKnoxCustomSettingGranted() {
        val permission = "com.samsung.android.knox.permission.KNOX_CUSTOM_SETTING"
        assertTrue(cdm.checkEnterprisePermission(permission))
    }

    @Test
    fun checkKnoxRestrictionManagementGranted() {
        val permission = "com.samsung.android.knox.permission.KNOX_RESTRICTION_MGMT"
        assertTrue(cdm.checkEnterprisePermission(permission))
    }

    @Test
    fun checkKnoxCustomSystemGranted() {
        val permission = "com.samsung.android.knox.permission.KNOX_CUSTOM_SYSTEM"
        assertTrue(cdm.checkEnterprisePermission(permission))
    }

    @Test
    fun checkKnoxAdvancedRestrictionGranted() {
        val permission = "com.samsung.android.knox.permission.KNOX_ADVANCED_RESTRICTION"
        assertTrue(cdm.checkEnterprisePermission(permission))
    }

    @Test
    fun checkKnoxPhoneRestrictionGranted() {
        val permission = "com.samsung.android.knox.permission.KNOX_PHONE_RESTRICTION"
        assertTrue(cdm.checkEnterprisePermission(permission))
    }
}