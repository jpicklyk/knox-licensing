package net.sfelabs.knoxmoduleshowcase.tests.tcp

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knoxmoduleshowcase.app.checkMethodExistence
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.tcp.DisableTcpDumpUseCase
import net.sfelabs.knox_tactical.domain.use_cases.tcp.EnableTcpDumpUseCase
import net.sfelabs.knox_tactical.domain.use_cases.tcp.IsTcpDumpEnabled
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
@SmallTest
//@TacticalSdkSuppress(minReleaseVersion = 100)
class TcpDumpTests {
    private val systemManager = KnoxModule.provideKnoxSystemManager()
    lateinit var context: Context
    lateinit var captureUri: Uri
    private lateinit var testScope: TestScope

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        captureUri = createCaptureUri()
        testScope = TestScope()
    }
    /*
    Where the value of captureFile.getAbsolutePath() is “/storage/emulated/0/ecap/any-2024-10-24-11-27-44-capture.pcap”

            String tcpCommand = "any" + " -B10 -C500 -w" + captureFile.getAbsolutePath() + " -s262144 -n";

     */

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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun enableTcpDump_anyB10S200C10NoFileSpecified() = testScope.runTest {
        val tcpCommand = "any" + " -B10 -s2000 -C10";
        val enableUseCase = EnableTcpDumpUseCase(systemManager)
        val result = enableUseCase.invoke(tcpCommand)
        assertTrue("Enabling TCP dump failed: ${result.getErrorOrNull()}",result is ApiResult.Success)
        advanceTimeBy(1000)
        val checkUseCase = IsTcpDumpEnabled(systemManager)
        val checkResult = checkUseCase.invoke()
        assertTrue("Error, TCP Dump is not running!", checkResult is ApiResult.Success && checkResult.data)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun enableTcpDump_anyB10S200C10WithFile() = testScope.runTest {
        assertMediaFileExists(context,captureUri)
        val path = getAbsolutePath(captureUri)
        println("Capture uri path: $path")
        val tcpCommand = "any" + " -B10 -s2000 -C10 -w /sdcard/Download/capture.pcap" //+ getAbsolutePath(captureUri)
        val enableUseCase = EnableTcpDumpUseCase(systemManager)
        val result = enableUseCase.invoke(tcpCommand)
        assertTrue("Enabling TCP dump failed: ${result.getErrorOrNull()}",result is ApiResult.Success)
        advanceTimeBy(1000)
        val checkUseCase = IsTcpDumpEnabled(systemManager)
        val checkResult = checkUseCase.invoke()
        assertTrue("Error, TCP Dump is not running! ${checkResult.getExceptionOrNull()}", checkResult is ApiResult.Success && checkResult.data)
    }

    private fun createCaptureUri() : Uri {
        var uri: Uri? = null
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "capture.pcap")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/cap")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        uri = context.contentResolver.insert(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            contentValues
        ) ?: throw IllegalStateException("Failed to create MediaStore URI")

        //Create an empty file
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

    @After
    fun cleanup() = runTest {
        DisableTcpDumpUseCase(systemManager).invoke()
        context.contentResolver.delete(captureUri, null, null)
    }
}