package com.example.fcmdemo

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.runBlocking

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val deviceToken = inputData.getString("deviceToken") ?: return Result.failure()
        val title = inputData.getString("title") ?: return Result.failure()
        val body = inputData.getString("body") ?: return Result.failure()

        Log.d("NotificationWorker", "Sending FCM Notification: $title - $body")

        runBlocking {
            try {
                FCMHelper.sendNotification(applicationContext, deviceToken, title, body)
            } catch (e: Exception) {
                Log.e("NotificationWorker", "Error sending notification", e)
                return@runBlocking Result.failure()
            }
        }

        return Result.success()
    }
}