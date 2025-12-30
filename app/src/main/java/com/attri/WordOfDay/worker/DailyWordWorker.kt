package com.attri.WordOfDay.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.attri.WordOfDay.MainActivity
import com.attri.WordOfDay.R
import com.attri.WordOfDay.data.repository.GeminiRepository
import com.attri.WordOfDay.data.repository.PreferencesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltWorker
class DailyWordWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: GeminiRepository,
    private val preferencesRepository: PreferencesRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val TAG = "DailyWordWorker"
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "doWork: Worker started.")
        return try {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            Log.d(TAG, "doWork: Resetting click count for today.")
            preferencesRepository.resetClickCount(today)

            Log.d(TAG, "doWork: Fetching new word from AI...")
            val result = repository.fetchNewWordFromAI()

            result.onSuccess { word ->
                Log.d(TAG, "doWork: Successfully fetched word: ${word.word}")
                preferencesRepository.incrementClickCount()
                showNotification(word.word, word.hindiMeaning)
                Log.d(TAG, "doWork: Notification should be shown.")
            }.onFailure { error ->
                Log.e(TAG, "doWork: Failed to fetch word.", error)
                return Result.retry()
            }

            Log.d(TAG, "doWork: Worker finished successfully.")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "doWork: Worker failed with an exception.", e)
            Result.failure()
        }
    }

    private fun showNotification(word: String, meaning: String) {
        val context = applicationContext
        val channelId = "daily_word_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Daily Word Notification"
            val descriptionText = "Notifications for the Word of the Day"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("Word of the Day: $word")
            .setContentText("Meaning: $meaning")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                NotificationManagerCompat.from(context).notify(1001, builder.build())
                Log.d(TAG, "showNotification: Notification posted.")
            } catch (e: SecurityException) {
                Log.e(TAG, "showNotification: SecurityException when posting notification.", e)
            }
        } else {
            Log.e(TAG, "showNotification: POST_NOTIFICATIONS permission not granted.")
        }
    }
}
