package net.sfelabs.common.core.ui

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState



@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MultiplePermissions() {
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    )

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner, effect = {
        val observer = LifecycleEventObserver { _, event ->
            when(event) {
                Lifecycle.Event.ON_START -> {
                    permissionState.launchMultiplePermissionRequest()
                }

                Lifecycle.Event.ON_CREATE -> TODO()
                Lifecycle.Event.ON_RESUME -> TODO()
                Lifecycle.Event.ON_PAUSE -> TODO()
                Lifecycle.Event.ON_STOP -> TODO()
                Lifecycle.Event.ON_DESTROY -> TODO()
                Lifecycle.Event.ON_ANY -> TODO()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    })

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        /*permissionState.permissions.forEach { it ->
            when (it.permission) {
                Manifest.permission.READ_EXTERNAL_STORAGE -> {
                    when {
                        it.hasPermission -> {
                            *//* Permission has been granted by the user.
                               You can use this permission to now acquire the location of the device.
                               You can perform some other tasks here.
                            *//*
                            Text(text = "Read Ext Storage permission has been granted")
                        }
                        it.shouldShowRationale -> {
                            *//*Happens if a user denies the permission two times

                             *//*
                            Text(text = "Read Ext Storage permission is needed")
                        }
                        !it.hasPermission && !it.shouldShowRationale -> {
                            *//* If the permission is denied and the should not show rationale
                                You can only allow the permission manually through app settings
                             *//*
                            Text(text = "Navigate to settings and enable the Storage permission")

                        }
                    }
                }
                Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                    when {
                        it.hasPermission -> {
                            *//* Permission has been granted by the user.
                               You can use this permission to now acquire the location of the device.
                               You can perform some other tasks here.
                            *//*
                            Text(text = "Location permission has been granted")
                        }
                        it.shouldShowRationale -> {
                            *//*Happens if a user denies the permission two times

                             *//*
                            Text(text = "Location permission is needed")

                        }
                        !it.hasPermission && !it.shouldShowRationale -> {
                            *//* If the permission is denied and the should not show rationale
                                You can only allow the permission manually through app settings
                             *//*
                            Text(text = "Navigate to settings and enable the Location permission")

                        }
                    }
                }
            }
        }*/
    }
}