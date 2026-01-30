package net.sfelabs.knoxmoduleshowcase.tests.knox_core

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_tactical.domain.use_cases.permissions.CheckEnterprisePermissionUseCase
import net.sfelabs.knox_tactical.domain.use_cases.permissions.KnoxPermissions
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class CheckKnoxPermissionsGranted {

    @Test
    fun checkKnoxCustomSettingGranted() = runTest {
        val result = CheckEnterprisePermissionUseCase().invoke(KnoxPermissions.KNOX_CUSTOM_SETTING)
        assertTrue(result is ApiResult.Success && result.data)
    }

    @Test
    fun checkKnoxRestrictionManagementGranted() = runTest {
        val result = CheckEnterprisePermissionUseCase().invoke(KnoxPermissions.KNOX_RESTRICTION_MGMT)
        assertTrue(result is ApiResult.Success && result.data)
    }

    @Test
    fun checkKnoxCustomSystemGranted() = runTest {
        val result = CheckEnterprisePermissionUseCase().invoke(KnoxPermissions.KNOX_CUSTOM_SYSTEM)
        assertTrue(result is ApiResult.Success && result.data)
    }

    @Test
    fun checkKnoxAdvancedRestrictionGranted() = runTest {
        val result = CheckEnterprisePermissionUseCase().invoke(KnoxPermissions.KNOX_ADVANCED_RESTRICTION)
        assertTrue(result is ApiResult.Success && result.data)
    }

    @Test
    fun checkKnoxPhoneRestrictionGranted() = runTest {
        val result = CheckEnterprisePermissionUseCase().invoke(KnoxPermissions.KNOX_PHONE_RESTRICTION)
        assertTrue(result is ApiResult.Success && result.data)
    }
}