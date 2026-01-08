package com.pepsigo.admin.utils

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationPermissionRequester(
    onComplete:  () -> Unit
) {
    val permissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

    LaunchedEffect(Unit) {
        if (!permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()
        } else {
            onComplete()
        }
    }

    when {
        permissionState.status.isGranted -> {
            onComplete()
        }

        permissionState.status.shouldShowRationale -> {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Allow Notifications") },
                text = { Text("Notifications help us alert you about updates.") },
                confirmButton = {
                    TextButton(onClick = { permissionState.launchPermissionRequest() }) {
                        Text("Allow")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onComplete) {
                        Text("Not now")
                    }
                }
            )
        }

        else -> {
            onComplete() // Permanently denied
        }
    }
}