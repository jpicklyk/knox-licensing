package net.sfelabs.core.domain.usecase

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.sfelabs.core.domain.usecase.executor.assertAllFailed
import net.sfelabs.core.domain.usecase.executor.assertAllSuccessful
import net.sfelabs.core.domain.usecase.executor.assertAnyFailed
import net.sfelabs.core.domain.usecase.executor.assertAnySuccessful
import net.sfelabs.core.domain.usecase.executor.assertNoneSuccessful
import net.sfelabs.core.domain.usecase.executor.assertNotSupported
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.model.DefaultApiError
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UseCaseExtensionsTest {

    @Test
    fun `assertAllSuccessful returns true when all results are successful`() {
        val results = listOf(
            ApiResult.Success(1),
            ApiResult.Success("test"),
            ApiResult.Success(true)
        )

        assertTrue(results.assertAllSuccessful())
    }

    @Test
    fun `assertAllSuccessful returns false when any result is not successful`() {
        val results = listOf(
            ApiResult.Success(1),
            ApiResult.Error(DefaultApiError.UnexpectedError()),
            ApiResult.Success(true)
        )

        assertFalse(results.assertAllSuccessful())
    }

    @Test
    fun `assertAllSuccessful returns false for empty list`() {
        val results = emptyList<ApiResult<*>>()

        assertFalse(results.assertAllSuccessful())
    }

    @Test
    fun `assertAnySuccessful returns true when at least one result is successful`() {
        val results = listOf(
            ApiResult.Error(DefaultApiError.UnexpectedError()),
            ApiResult.Success("test"),
            ApiResult.Error(DefaultApiError.UnexpectedError())
        )

        assertTrue(results.assertAnySuccessful())
    }

    @Test
    fun `assertAnySuccessful returns false when no results are successful`() {
        val results = listOf(
            ApiResult.Error(DefaultApiError.UnexpectedError()),
            ApiResult.Error(DefaultApiError.UnexpectedError()),
            ApiResult.NotSupported
        )

        assertFalse(results.assertAnySuccessful())
    }

    @Test
    fun `assertNoneSuccessful returns true when no results are successful`() {
        val results = listOf(
            ApiResult.Error(DefaultApiError.UnexpectedError()),
            ApiResult.NotSupported,
            ApiResult.Error(DefaultApiError.UnexpectedError())
        )

        assertTrue(results.assertNoneSuccessful())
    }

    @Test
    fun `assertNoneSuccessful returns false when any result is successful`() {
        val results = listOf(
            ApiResult.Error(DefaultApiError.UnexpectedError()),
            ApiResult.Success(1),
            ApiResult.Error(DefaultApiError.UnexpectedError())
        )

        assertFalse(results.assertNoneSuccessful())
    }

    @Test
    fun `assertAllFailed returns true when all results are errors`() {
        val results = listOf(
            ApiResult.Error(DefaultApiError.UnexpectedError()),
            ApiResult.Error(DefaultApiError.UnexpectedError()),
            ApiResult.Error(DefaultApiError.UnexpectedError())
        )

        assertTrue(results.assertAllFailed())
    }

    @Test
    fun `assertAllFailed returns false when any result is not an error`() {
        val results = listOf(
            ApiResult.Error(DefaultApiError.UnexpectedError()),
            ApiResult.Success(1),
            ApiResult.Error(DefaultApiError.UnexpectedError())
        )

        assertFalse(results.assertAllFailed())
    }

    @Test
    fun `assertAnyFailed returns true when at least one result is an error`() {
        val results = listOf(
            ApiResult.Success(1),
            ApiResult.Error(DefaultApiError.UnexpectedError()),
            ApiResult.Success(2)
        )

        assertTrue(results.assertAnyFailed())
    }

    @Test
    fun `assertAnyFailed returns false when no results are errors`() {
        val results = listOf(
            ApiResult.Success(1),
            ApiResult.Success(2),
            ApiResult.NotSupported
        )

        assertFalse(results.assertAnyFailed())
    }

    @Test
    fun `assertNotSupported returns true when any result is NotSupported`() {
        val results = listOf(
            ApiResult.Success(1),
            ApiResult.NotSupported,
            ApiResult.Error(DefaultApiError.UnexpectedError())
        )

        assertTrue(results.assertNotSupported())
    }

    @Test
    fun `assertNotSupported returns false when no results are NotSupported`() {
        val results = listOf(
            ApiResult.Success(1),
            ApiResult.Error(DefaultApiError.UnexpectedError()),
            ApiResult.Success(2)
        )

        assertFalse(results.assertNotSupported())
    }

    @Test
    fun `empty list returns false for all assertions`() {
        val results = emptyList<ApiResult<*>>()

        assertFalse(results.assertAllSuccessful())
        assertFalse(results.assertAnySuccessful())
        assertFalse(results.assertNoneSuccessful())
        assertFalse(results.assertAllFailed())
        assertFalse(results.assertAnyFailed())
        assertFalse(results.assertNotSupported())
    }
}