package com.labbany.labbany.fcm

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.labbany.labbany.R
import com.labbany.labbany.ui.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {
    private var numMessages = 0

    lateinit var data: MutableMap<String, String>

    override fun handleIntent(intent: Intent) {
      //  Log.e(TAG, "handleIntent: mmr")
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.data["title"]//remoteMessage.notification!!.title
        val body = remoteMessage.data["body"]//remoteMessage.notification!!.body

      //  Log.e(TAG, "onMessageReceived: mmr")

        sendNotification(title, body)

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun sendNotification(title: String?, body: String?) {


        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val notificationBuilder =
            NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .apply {
                    setContentTitle(title)
                    setContentText(body)
                    setAutoCancel(true)
                    setSound(
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    )
                    setContentIntent(pendingIntent)
                    setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                    color = getColor(R.color.yellow_dark)
                    setLights(Color.RED, 1000, 300)
                    setDefaults(Notification.DEFAULT_VIBRATE)
                    setNumber(++numMessages)
                    setSmallIcon(R.drawable.logo_1)
                }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                getString(R.string.default_notification_channel_id),
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.setShowBadge(true)
            channel.canShowBadge()
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500)
            notificationManager.createNotificationChannel(channel)

        }
        notificationManager.notify(0, notificationBuilder.build())
    }


    companion object {
        private const val CHANNEL_NAME = "FCM"
        private const val TAG = "MyFirebase"
    }

}