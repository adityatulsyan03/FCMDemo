package com.example.fcmdemo.Screens

import android.Manifest
import android.content.Context
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
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.fcmdemo.NotificationWorker
import com.example.fcmdemo.checkNotificationPermission
import java.util.Calendar
import java.util.concurrent.TimeUnit

@Composable
fun MultipleMobileScreen(context: Context, fcmToken: String?, tokens: List<String>) {
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
    var year by remember { mutableStateOf(2025) }
    var month by remember { mutableStateOf(2) }
    var day by remember { mutableStateOf(12) }
    var hour by remember { mutableStateOf(20) }
    var minute by remember { mutableStateOf(20) }

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
                onValueChange = { title.value = it },
                label = { Text("Title") })
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = body.value,
                onValueChange = { body.value = it },
                label = { Text("Body") })

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (!hasPermission) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    Log.d("FCM", "Token: $fcmToken")
                    fcmToken?.let { token ->
                        scheduleNotification(
                            context,
                            token,
                            title.value,
                            body.value,
                            year,
                            month,
                            day,
                            hour,
                            minute
                        )
                    }

                    tokens.forEach {
                        scheduleNotification(
                            context,
                            it, title.value, body.value, year, month, day, hour, minute
                        )
                    }
                }
            }) {
                Text("Schedule Notification")
            }
        }
    }
}

fun scheduleNotification(
    context: Context,
    deviceToken: String,
    title: String,
    body: String,
    year: Int,
    month: Int,
    day: Int,
    hour: Int,
    minute: Int
) {
    val calendar = Calendar.getInstance()
    val delay = calendar.apply {
        set(
            year,
            month - 1,
            day,
            hour,
            minute
        )
    }.timeInMillis - System.currentTimeMillis()

    if (delay <= 0) return

    val data = workDataOf("deviceToken" to deviceToken, "title" to title, "body" to body)
    val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .setInputData(data)
        .build()

    WorkManager.getInstance(context).enqueue(workRequest)
}