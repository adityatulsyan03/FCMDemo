package com.example.fcmdemo.Screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.fcmdemo.FCMHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ValidationScreen(context: Context, fcmToken: String?) {
    var enteredToken by remember { mutableStateOf("") }
    var validationResult by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Enter FCM Token:", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = enteredToken,
                onValueChange = { enteredToken = it },
                label = { Text("FCM Token") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    val isValid = FCMHelper.isValidFCMToken(context, enteredToken)
                    validationResult = if (isValid) "Valid Token ✅" else "Invalid Token ❌"
                }
            }) {
                Text("Validate Token")
            }
            Spacer(modifier = Modifier.height(16.dp))
            validationResult?.let {
                Text(it, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}