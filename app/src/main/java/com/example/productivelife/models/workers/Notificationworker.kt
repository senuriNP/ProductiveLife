package com.example.productivelife.models.workers

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.productivelife.R
import kotlin.random.Random

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val title = inputData.getString("reminder_title") ?: return Result.failure()

        // Check for notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (applicationContext.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                return Result.failure() // Handle this as needed
            }
        }
        showNotification(title)
        return Result.success()
    }

    private fun showNotification(title: String) {
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        val notificationId = Random.nextInt()

        val notification = NotificationCompat.Builder(applicationContext, "reminder_channel")
            .setContentTitle("Reminder")
            .setContentText(title)
            .setSmallIcon(R.drawable.picon1) // Make sure to add your icon
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}
