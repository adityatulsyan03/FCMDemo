package com.example.fcmdemo

import android.content.Context
import android.util.Log
import com.google.auth.oauth2.GoogleCredentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.InputStream

object FCMHelper {
    private const val FCM_SEND_URL = "https://fcm.googleapis.com/v1/projects/fcmdemo-abe1c/messages:send"

    // ✅ Function to validate FCM Token
    suspend fun isValidFCMToken(context: Context, token: String): Boolean {
        return try {
            val accessToken = getAccessToken(context)

            val jsonBody = JSONObject().apply {
                put("message", JSONObject().apply {
                    put("token", token)
                    put("notification", JSONObject().apply {
                        put("title", "Test")
                        put("body", "Checking if FCM token is valid")
                    })
                })
            }

            val client = OkHttpClient()
            val requestBody = RequestBody.create(
                "application/json".toMediaTypeOrNull(),
                jsonBody.toString()
            )

            val request = Request.Builder()
                .url(FCM_SEND_URL)
                .post(requestBody)
                .addHeader("Authorization", "Bearer $accessToken")
                .addHeader("Content-Type", "application/json")
                .build()

            withContext(Dispatchers.IO) {
                val response = client.newCall(request).execute()
                Log.d("FCMHelper", "Validation Response: ${response.code}")
                response.isSuccessful  // ✅ If successful, the token is valid
            }
        } catch (e: Exception) {
            Log.e("FCMHelper", "Error validating FCM token", e)
            false  // ❌ Invalid token
        }
    }

    suspend fun sendNotification(context: Context, deviceToken: String, title: String, body: String) {
        try {
            val accessToken = getAccessToken(context)

            val jsonBody = JSONObject().apply {
                put("message", JSONObject().apply {
                    put("token", deviceToken)
                    put("notification", JSONObject().apply {
                        put("title", title)
                        put("body", body)
                    })
                })
            }

            val client = OkHttpClient()
            val requestBody = RequestBody.create(
                "application/json".toMediaTypeOrNull(),
                jsonBody.toString()
            )

            val request = Request.Builder()
                .url(FCM_SEND_URL)
                .post(requestBody)
                .addHeader("Authorization", "Bearer $accessToken")
                .addHeader("Content-Type", "application/json")
                .build()

            withContext(Dispatchers.IO) {
                val response = client.newCall(request).execute()
                Log.d("FCMHelper", "Response Code: ${response.code}")
                Log.d("FCMHelper", "Response Body: ${response.body?.string()}")
            }

        } catch (e: Exception) {
            Log.e("FCMHelper", "Error sending FCM message", e)
        }
    }

    private fun getAccessToken(context: Context): String {
        val assetManager = context.assets
        val inputStream: InputStream = assetManager.open("firebaseFCM.json")

        val credentials = GoogleCredentials
            .fromStream(inputStream)
            .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))

        credentials.refreshIfExpired()
        return credentials.accessToken.tokenValue
    }
}