package com.example.fcmdemo.Screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.fcmdemo.FCMHelper
import com.example.fcmdemo.checkNotificationPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun UserMobileScreen(context: Context, fcmToken: String?) {
    var tokenText by remember { mutableStateOf("Fetching token...") }
    var hasPermission by remember { mutableStateOf(checkNotificationPermission(context)) }

    LaunchedEffect(fcmToken) {
        fcmToken?.let { tokenText = it }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (!isGranted) {
            Log.e("FCM", "Notification permission denied by user")
        }
    }

    val title = remember { mutableStateOf("") }
    val body = remember { mutableStateOf("") }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("FCM Token:", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(tokenText, modifier = Modifier.padding(16.dp))
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = title.value,
                onValueChange = {
                    title.value = it
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = body.value,
                onValueChange = {
                    body.value = it
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (!hasPermission) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    Log.d("FCM", "Token: $fcmToken")
                    fcmToken?.let { token ->
                        CoroutineScope(Dispatchers.IO).launch {
                            FCMHelper.sendNotification(context, token,title.value,body.value)
                        }
                    }
                }
            }) {
                Text("Send Notification")
            }
        }
    }
}