package ru.gb.weather

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val PUSH_KEY_TITLE = "title"
        private const val PUSH_KEY_MESSAGE = "message"
        private const val NOTIFICATION_ID = 37
        private const val WARNING_CHANNEL_ID = "WARNING_CHANNEL"
        private const val COMMON_CHANNEL_ID = "COMMON_CHANNEL"
        private const val WARNING_CHANNEL_TITLE = "Предупреждения"
        private const val WARNING_CHANNEL_DESCRIPTION = "Предупреждения о погодных условиях"
        private const val COMMON_CHANNEL_TITLE = "Оповещения"
        private const val COMMON_CHANNEL_DESCRIPTION = ""
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val remoteMessageData = remoteMessage.data
        val channelId = remoteMessage.notification?.channelId ?: COMMON_CHANNEL_ID

        if (remoteMessageData.isNotEmpty()) {
            handleDataMessage(remoteMessageData.toMap(), channelId)
        }
    }

    override fun onNewToken(token: String) {
        // Новый токен
    }

    private fun handleDataMessage(data: Map<String, String>, channelId: String) {

        val title = data[PUSH_KEY_TITLE]
        val message = data[PUSH_KEY_MESSAGE]

        if (!title.isNullOrBlank() && !message.isNullOrBlank()) {
            showNotification(title, message, channelId)
        }
    }

    private fun showNotification(title: String, message: String, channelId: String) {
        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId).apply {
            setSmallIcon(R.drawable.ic_kotlin_logo)
            setContentTitle(title)
            setContentText(message)
            priority = NotificationCompat.PRIORITY_DEFAULT
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            when (channelId) {
                WARNING_CHANNEL_ID -> {
                    createNotificationChannel(
                        notificationManager,
                        WARNING_CHANNEL_ID,
                        WARNING_CHANNEL_TITLE,
                        WARNING_CHANNEL_DESCRIPTION,
                        NotificationManager.IMPORTANCE_HIGH
                    )
                }
                else -> {
                    createNotificationChannel(
                        notificationManager,
                        COMMON_CHANNEL_ID,
                        COMMON_CHANNEL_TITLE,
                        COMMON_CHANNEL_DESCRIPTION
                    )
                }
            }
        }

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun createNotificationChannel(
        notificationManager: NotificationManager,
        channelId: String,
        name: String,
        descriptionText: String,
        importance: Int = NotificationManager.IMPORTANCE_DEFAULT
    ) {

        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }

        notificationManager.createNotificationChannel(channel)
    }
}