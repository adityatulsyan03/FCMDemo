package com.example.fcmdemo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.fcmdemo.Screens.MultipleMobileScreen
import com.example.fcmdemo.Screens.UserMobileScreen
import com.example.fcmdemo.Screens.ValidationScreen
import com.example.fcmdemo.ui.theme.FCMDemoTheme
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    private var fcmToken: String? by mutableStateOf(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get FCM token for the device
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    fcmToken = task.result
                    Log.d("FCM Token", "Device Token: $fcmToken")
                } else {
                    Log.e("FCM Token", "Fetching FCM token failed", task.exception)
                }
            }

        setContent {
            FCMDemoTheme {
                val cnt = 1
                if (cnt == 1) {
                    UserMobileScreen(this, fcmToken)
                } else if (cnt == 2) {
                    ValidationScreen(this, fcmToken)
                } else {
                    val token =
                        listOf("eO_-2OGmQkKu2Rrhzfw7bd:APA91bES1NIzsc1wsk5y62At7rE60pAzHJ3YM8gqg1GoYXPlVODzcAUW2M6Hhq6g9Z9vsuRIa6YP80-GUQ8FhjoKV2SLd2gE-cAE7gn9od2dJYswsK7NBCw\n")
                    MultipleMobileScreen(this, fcmToken, token)
                }
            }
        }
    }
}