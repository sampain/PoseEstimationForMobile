package com.epmus.mobile

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.epmus.mobile.ui.login.LoginActivity


class AlarmReceiver : BroadcastReceiver() {
    private var mNotificationManager: NotificationManager? = null

    override fun onReceive(context: Context, intent: Intent) {
        mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        deliverNotification(context)
    }

    private fun deliverNotification(context: Context) {
        val contentIntent = Intent(context, LoginActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            contentIntent,
            FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(
            context,
            PRIMARY_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.arms)
            .setContentTitle("iPhysio")
            .setContentText("Il est temps de faire votre exercice!")
            .setContentIntent(contentPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        mNotificationManager!!.notify(
            NOTIFICATION_ID,
            builder.build()
        )
    }

    companion object {
        private const val NOTIFICATION_ID = 0
        private const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    }
}