package net.sfelabs.knoxmoduleshowcase.manual_tests.experimental

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.core.exceptions.UnavailableException
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CameraDepthAvailabilityTests {
    private lateinit var context: Context
    private lateinit var depthCamera: DepthCamera

    companion object {
        private const val CAMERA_PERMISSION = "android.permission.CAMERA"
    }
    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(CAMERA_PERMISSION)

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        depthCamera = DepthCamera(context)
    }

    @Test
    fun testDepthCameraExists() {
        assertTrue("Camera permission should be granted",
            ContextCompat.checkSelfPermission(context, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED)

        val hasDepthCamera = depthCamera.hasDepthCamera()
        println("Device has depth camera: $hasDepthCamera")
        // Note: This assertion might fail on devices without a depth camera
        assertTrue("Device does not have a depth camera (ToF)", hasDepthCamera)
    }

    @Test
    fun testLidarCapabilities() {
        val hasLidar = depthCamera.hasLidarCapabilities()
        println("Device has LiDAR capabilities: $hasLidar")
        // Note: This assertion might fail on devices without LiDAR
        assertTrue("Device does not have LiDAR capabilities", hasLidar)
    }
}


class DepthCamera(private val context: Context) {
    private val cameraManager: CameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    fun hasDepthCamera(): Boolean {
        return findDepthCameraId() != null
    }

    fun hasLidarCapabilities(): Boolean {
        return isArCoreInstalled() && hasDepthCapabilities()
    }

    private fun findDepthCameraId(): String? {
        return cameraManager.cameraIdList.find { cameraId ->
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            val capabilities = characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)
            capabilities?.contains(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT) == true
        }
    }

    private fun isArCoreInstalled(): Boolean {
        return when (ArCoreApk.getInstance().checkAvailability(context)) {
            ArCoreApk.Availability.SUPPORTED_INSTALLED -> true
            else -> false
        }
    }

    private fun hasDepthCapabilities(): Boolean {
        if (!isArCoreInstalled()) return false

        return try {
            val session = Session(context)
            val config = session.config
            config.depthMode == Config.DepthMode.AUTOMATIC
        } catch (e: UnavailableException) {
            false
        }
    }
}