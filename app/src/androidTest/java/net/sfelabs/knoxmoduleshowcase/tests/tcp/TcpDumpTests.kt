package net.sfelabs.knoxmoduleshowcase.tests.tcp

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knoxmoduleshowcase.app.checkMethodExistence
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.tcp.DisableTcpDumpUseCase
import net.sfelabs.knox_tactical.domain.use_cases.tcp.EnableTcpDumpUseCase
import net.sfelabs.knox_tactical.domain.use_cases.tcp.IsTcpDumpEnabled
import org.junit.Before
import org.junit.Test
import org.junit.runner.OrderWith
import org.junit.runner.RunWith
import org.junit.runner.manipulation.Alphanumeric

@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 100)
@OrderWith(Alphanumeric::class)
class TcpDumpTests {
    private val systemManager = KnoxModule.provideKnoxSystemManager()
    private val filename = "capture_test.pcap"
    lateinit var context: Context
    lateinit var captureUri: Uri

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        captureUri = createCaptureUri()
    }

    @Test
    fun isTcpDumpEnabled_Exists() = runTest {
        assert(checkMethodExistence(systemManager::class,"isTcpDumpEnabled"))
    }

    @Test
    fun enableTcpDump_Exists() = runTest {
        assert(checkMethodExistence(systemManager::class,"enableTcpDump"))
    }

    @Test
    fun disableTcpDump_Exists() = runTest {
        assert(checkMethodExistence(systemManager::class,"disableTcpDump"))
    }

    @Test
    fun test1_enableTcpDump_anyB10S200C10NoFileSpecified() = runTest {
        val tcpCommand = "any" + " -B10 -s2000 -C10"
        val enableUseCase = EnableTcpDumpUseCase(systemManager)
        val result = enableUseCase.invoke(tcpCommand)
        assert(result is ApiResult.Success)
    }

    @Test
    fun test2_checkTcpDumpIsRunningReturnsTrue() = runTest {
        val checkUseCase = IsTcpDumpEnabled(systemManager)
        val checkResult = checkUseCase.invoke()
        assert(checkResult is ApiResult.Success && checkResult.data)
    }

    @Test
    fun test3_disableTcpDump() = runTest {
        val disableUseCase = DisableTcpDumpUseCase(systemManager)
        val result = disableUseCase.invoke()
        assert(result is ApiResult.Success)
    }

    @Test
    fun test4_checkTcpDumpIsRunningReturnsFalse() = runTest {
        val checkUseCase = IsTcpDumpEnabled(systemManager)
        val checkResult = checkUseCase.invoke()
        assert(checkResult is ApiResult.Success && !checkResult.data)
    }

    @Test
    fun test5_enableTcpDump_anyB10S200C10WithFile() = runTest {
        assertMediaFileExists(context,captureUri)
        val path = getAbsolutePath(captureUri)
        println("Capture uri path: $path")
        val tcpCommand = "any" + " -B10 -s2000 -C10 -w/sdcard/Download/${filename}" //+ getAbsolutePath(captureUri)
        val enableUseCase = EnableTcpDumpUseCase(systemManager)
        val result = enableUseCase.invoke(tcpCommand)
        assertTrue("Enabling TCP Dump failed.  Cmd used: $tcpCommand", result is ApiResult.Success)
    }

    @Test
    fun test6_checkTcpDumpIsRunningReturnsTrue() = runTest {
        delay(2000)
        val checkUseCase = IsTcpDumpEnabled(systemManager)
        val checkResult = checkUseCase.invoke()
        assert(checkResult is ApiResult.Success && checkResult.data)

    }

    @Test
    fun test7_disableTcpDump() = runTest {
        val disableUseCase = DisableTcpDumpUseCase(systemManager)
        val result = disableUseCase.invoke()
        assert(result is ApiResult.Success)
        //Can delete the pcap file here
        context.contentResolver.delete(captureUri, null, null)
    }

    @Test
    fun test8_checkTcpDumpIsRunningReturnsFalse() = runTest {
        val checkUseCase = IsTcpDumpEnabled(systemManager)
        val checkResult = checkUseCase.invoke()
        assert(checkResult is ApiResult.Success && !checkResult.data)
    }

    @Test
    fun zzz_cleanup() = runTest {
        context.contentResolver.delete(captureUri, null, null)
    }

    private fun createCaptureUri(): Uri {
        // First check if file already exists
        val projection = arrayOf(MediaStore.MediaColumns._ID)
        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME} = ? AND " +
                "${MediaStore.MediaColumns.RELATIVE_PATH} = ?"
        val selectionArgs = arrayOf(
            filename,
            "${Environment.DIRECTORY_DOWNLOADS}/"
        )

        // Query existing file
        context.contentResolver.query(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                // File exists, return its URI
                val id = cursor.getLong(0)
                return ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI, id)
            }
        }

        // File doesn't exist, create it
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/cap")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = context.contentResolver.insert(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            contentValues
        ) ?: throw IllegalStateException("Failed to create MediaStore URI")

        // Create an empty file
        try {
            context.contentResolver.openOutputStream(uri).use { outputStream ->
                outputStream?.close()
            }
        } catch (e: Exception) {
            throw IllegalStateException("Failed to create empty file at URI: $uri", e)
        }

        return uri
    }


    private fun assertMediaFileExists(context: Context, mediaFileUri: Uri) {
        val cursor = context.contentResolver.query(
            mediaFileUri,
            arrayOf(MediaStore.MediaColumns.DATA),
            null,
            null,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                // File exists
                // You can optionally access the file path using:
                // val filePath = it.getString(it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
            } else {
                // File does not exist
                throw AssertionError("Media file does not exist at URI: $mediaFileUri")
            }
        } ?: throw AssertionError("Failed to query MediaStore for URI: $mediaFileUri")
    }

    private fun getAbsolutePath(uri: Uri): String? {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA)
                if (columnIndex != -1) {
                    return cursor.getString(columnIndex)
                }
            }
        }
        return null
    }

}