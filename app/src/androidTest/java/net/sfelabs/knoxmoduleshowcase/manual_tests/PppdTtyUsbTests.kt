package net.sfelabs.knoxmoduleshowcase.manual_tests

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import junit.framework.TestCase
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.model.AdbHeader
import net.sfelabs.knox_tactical.domain.use_cases.adb.ExecuteAdbCommandUseCase
import net.sfelabs.knox_tactical.domain.use_cases.adb.StopPppdUseCase
import net.sfelabs.knoxmoduleshowcase.R
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.io.File
import java.io.FileOutputStream
import java.net.NetworkInterface

/**
 * This test class requires that the device be setup to communicate over a null modem cable in the
 * ttyUSB mode.  Having the connections the wrong way will setup ttyACM0 instead of USB.  For testing
 * please ensure you have copied the options file in the res folder to /sdcard/options to pass to the
 * ppp command.
 *
 * Tests will be run in ascending order based on the function name.  Be careful inserting new tests
 * and ensure naming is correctly ordered to properly clean up or undo changes.
 */

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@TacticalSdkSuppress(minReleaseVersion = 110)
class PppdTtyUsbTests {
    private val tag = "TTYUSBTEST"
    private val optionsFileName = "options"
    private val downloadDirectoryPath = "/sdcard/Download"
    private lateinit var context: Context
    private lateinit var optionsFileLocation: File

//    @Rule @JvmField
//    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule
//        .grant(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        // Use MediaStore API for Android 14+ compatibility
        optionsFileLocation = copyFileToDownloadsViaMediaStore(context, R.raw.options, optionsFileName)
        // Assert file copied successfully
        TestCase.assertTrue(optionsFileLocation.exists())
    }

    @Test
    fun checkForTtyUSB0() {
        val usbFile = File("/dev/ttyUSB0")
        assert(usbFile.exists())
    }

    @Test
    fun checkThatTtyACM0DoesNotExist() {
        val acmFile = File("/dev/ttyACM0")
        assert(!acmFile.exists())
    }

    @Test
    fun setupPpp() = runBlocking<Unit> {
        val useCase = ExecuteAdbCommandUseCase()
        val optionsFile = File("$downloadDirectoryPath/$optionsFileName")
        assertTrue("Options file was not copied to $downloadDirectoryPath/$optionsFileName!",optionsFile.exists())
        //KnoxCustomManagerService: executeAdbCommand - java.lang.IllegalArgumentException: value of system property 'knoxsdk.tac.body' is longer than 91 bytes: /dev/ttyUSB0 file /storage/emulated/0/Android/data/net.sfelabs.knoxmoduleshowcase/files/options
        //val result = useCase.invoke(net.sfelabs.knox_tactical.domain.model.AdbHeader.PPPD, "/dev/ttyUSB0 file $optionsFileLocation")
        val result = useCase.invoke(AdbHeader.PPPD, "/dev/ttyUSB0 file $downloadDirectoryPath/$optionsFileName")
        //val result = useCase.invoke(net.sfelabs.knox_tactical.domain.model.AdbHeader.PPPD, "/dev/ttyUSB0 file /sdcard/options")
        assert(result is ApiResult.Success)
    }



    @Test
    fun testPpp0InterfaceExists() {
        //Not the best way to handle the situation but good enough for now.  Need to wait for
        // interface availability which takes a couple of seconds.
        Thread.sleep(5000)
        try {
            TestCase.assertTrue("Interface ppp0 was not found", isPpp0InterfaceUp())
        } catch (e: Exception) {
            Log.e(tag, e.message, e)
            TestCase.assertTrue(e.message, false)
        }
    }

    @Test
    fun testStopPppd() = runBlocking<Unit> {
        Thread.sleep(1000)
        val useCase = StopPppdUseCase()
        val result = useCase.invoke()
        delay(1000)
        TestCase.assertTrue(
            "Knox API stopPPPD() was not successful.  $result",
            result is ApiResult.Success
        )
        assertFalse(
            "stopPPPD Api did not stop service.  Interface PPP0 is still available!",
            isPpp0InterfaceUp()
        )
    }

    /**
     * Copies file from raw resources to Downloads directory via MediaStore API.
     * Returns a File object pointing to /sdcard/Download/[filename] for use with Knox SDK.
     * This approach is compatible with Android 14+ scoped storage while keeping path short.
     * Only creates the file if it doesn't already exist.
     */
    private fun copyFileToDownloadsViaMediaStore(
        context: Context,
        rawResourceId: Int,
        targetFileName: String
    ): File {
        val targetFile = File("$downloadDirectoryPath/$targetFileName")

        // Check if file already exists
        if (targetFile.exists()) {
            Log.d(tag, "File already exists at $targetFile, skipping copy")
            return targetFile
        }

        // Query to check if MediaStore entry exists
        val projection = arrayOf(MediaStore.MediaColumns._ID)
        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME} = ? AND " +
                "${MediaStore.MediaColumns.RELATIVE_PATH} = ?"
        val selectionArgs = arrayOf(
            targetFileName,
            "${Environment.DIRECTORY_DOWNLOADS}/"
        )

        var existingUri: Uri? = null
        context.contentResolver.query(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(0)
                existingUri = ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI, id)
            }
        }

        // If MediaStore entry exists but file doesn't, use existing entry
        val uri = existingUri ?: run {
            // Create new MediaStore entry - use application/octet-stream to prevent extension
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, targetFileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/octet-stream")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            context.contentResolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                contentValues
            ) ?: throw IllegalStateException("Failed to create MediaStore URI for $targetFileName")
        }

        // Write file content
        context.contentResolver.openOutputStream(uri).use { outputStream ->
            context.resources.openRawResource(rawResourceId).use { inputStream ->
                inputStream.copyTo(outputStream!!)
            }
        }

        Log.d(tag, "File created at $targetFile")
        return targetFile
    }

    @Deprecated("Use copyFileToDownloadsViaMediaStore for Android 14+ compatibility")
    private fun copyFileFromRawToSDCard(
        context: Context,
        rawResourceId: Int,
        targetDirectory: File,
        targetFileName: String
    ) {
        val inputStream = context.resources.openRawResource(rawResourceId)
        val outputFile = File(targetDirectory, targetFileName)
        val outputStream = FileOutputStream(outputFile)
        val buffer = ByteArray(4 * 1024)
        var read: Int
        while (inputStream.read(buffer).also { read = it } != -1) {
            outputStream.write(buffer, 0, read)
        }
        outputStream.flush()
        inputStream.close()
        outputStream.close()
    }

    private fun isPpp0InterfaceUp(): Boolean {
        val interfaces = NetworkInterface.getNetworkInterfaces()
        if(interfaces == null) {
            Log.e(tag, "Unable to retrieve any network interfaces")
            return false
        } else {
            var count = 0
            while (interfaces.hasMoreElements()) {
                val ne = interfaces.nextElement()
                if (ne.displayName.startsWith("ppp", true)) {
                    count++
                    val message = StringBuilder("------------------\n")
                    message.append("Checking interface: ${ne.displayName}\n")
                    message.append("IP addresses assigned to this interface are:\n")
                    val addresses = ne.inetAddresses
                    while (addresses.hasMoreElements()) {
                        message.append(addresses.nextElement().toString()+"\n")
                    }

                    message.append("------------------")
                    Log.d(tag, message.toString())
                }
            }
            if(count == 0) {
                Log.d(tag, "No PPP interfaces connected")
                return false
            }
            return true
        }
    }
}