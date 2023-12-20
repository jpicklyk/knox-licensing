package net.sfelabs.knox_tactical.domain.use_cases.sim

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.core.domain.ApiResult
import net.sfelabs.core.domain.UiText
import javax.inject.Inject

class GetSimPowerStateUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(): ApiCall<ApiResult<Boolean>> {
        return coroutineScope {
            try {

                ApiCall.Success(
                    data = ApiResult(false, false)
                )
            } catch (e: SecurityException) {
                ApiCall.Error(
                    UiText.DynamicString(
                        e.message ?: "Calling application does not have the required permission"
                    )
                )
            } catch (e: NoSuchMethodError) {
                ApiCall.NotSupported
            }
        }


    }

}