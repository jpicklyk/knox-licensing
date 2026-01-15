package net.sfelabs.knoxmoduleshowcase.tests.audit

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.runBlocking
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_enterprise.domain.use_cases.audit.DisableAuditLogUseCase
import net.sfelabs.knox_enterprise.domain.use_cases.audit.EnableAuditLogUseCase
import net.sfelabs.knox_enterprise.domain.use_cases.audit.IsAuditLogEnabledUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class AuditLoggingTests {

    companion object {
        private const val TAG = "AuditLoggingTests"
    }

    @Test
    fun enableAuditLog() = runBlocking {
        val result = EnableAuditLogUseCase().invoke()
        Log.d(TAG, "EnableAuditLogUseCase result: $result")
        assert(result is ApiResult.Success) { "EnableAuditLogUseCase failed: $result" }

        val isEnabled = IsAuditLogEnabledUseCase().invoke()
        Log.d(TAG, "IsAuditLogEnabledUseCase result: $isEnabled")
        assert(isEnabled is ApiResult.Success && isEnabled.data) {
            "IsAuditLogEnabledUseCase failed or returned false: $isEnabled"
        }
    }

    @Test
    fun disableAuditLog() = runBlocking {
        val result = DisableAuditLogUseCase().invoke()
        Log.d(TAG, "DisableAuditLogUseCase result: $result")
        assert(result is ApiResult.Success) { "DisableAuditLogUseCase failed: $result" }

        val isEnabled = IsAuditLogEnabledUseCase().invoke()
        Log.d(TAG, "IsAuditLogEnabledUseCase result: $isEnabled")
        assert(isEnabled is ApiResult.Success && !isEnabled.data) {
            "IsAuditLogEnabledUseCase failed or returned true: $isEnabled"
        }
    }
}