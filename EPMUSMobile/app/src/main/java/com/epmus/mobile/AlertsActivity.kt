package com.epmus.mobile

import android.app.*
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.epmus.mobile.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_alerts.*
import java.util.*


class AlertsActivity : AppCompatActivity() {

    private var mNotificationManager: NotificationManager? = null

    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alerts)
        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val alarmToggle: ToggleButton = findViewById(R.id.btn_notify)

        // Set up the Notification Broadcast Intent.
        val notifyIntent = Intent(this, AlarmReceiver::class.java)
        val alarmUp = PendingIntent.getBroadcast(
            this, NOTIFICATION_ID,
            notifyIntent, PendingIntent.FLAG_NO_CREATE
        ) != null
        alarmToggle.isChecked = alarmUp
        val notifyPendingIntent = PendingIntent.getBroadcast(
            this, NOTIFICATION_ID, notifyIntent,
            FLAG_UPDATE_CURRENT
        )
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        // Set the click listener for the toggle button.
        alarmToggle.setOnCheckedChangeListener { buttonView, isChecked ->
            val toastMessage: String
            toastMessage = if (isChecked) {
                val repeatInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES
                val triggerTime = (SystemClock.elapsedRealtime()
                        + repeatInterval)

                // If the Toggle is turned on, set the repeating alarm with
                // a 15 minute interval.
                alarmManager.setInexactRepeating(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerTime, repeatInterval,
                    notifyPendingIntent
                )
                // Set the toast message for the "on" case.
                "getString(R.string.alarm_on_toast)"
            } else {
                // Cancel notification if the alarm is turned off.
                mNotificationManager!!.cancelAll()
                alarmManager.cancel(notifyPendingIntent)
                // Set the toast message for the "off" case.
                "getString(R.string.alarm_off_toast)"
            }

            // Show a toast to say the alarm is turned on or off.
            Toast.makeText(
                this, toastMessage,
                Toast.LENGTH_SHORT
            ).show()
        }

        // Create the notification channel.
        createNotificationChannel()

        /*notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
    PendingIntent.getBroadcast(this, NOTIFICATION_ID, intent, FLAG_UPDATE_CURRENT)
}

alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

val calendar: Calendar = Calendar.getInstance().apply {
    timeInMillis = System.currentTimeMillis()
    set(Calendar.HOUR_OF_DAY, 8)
    set(Calendar.MINUTE, 30)
}

alarmMgr?.setRepeating(
    AlarmManager.RTC_WAKEUP,
    calendar.timeInMillis,
    1000,
    alarmIntent
)*/
    }

    /**
     * Creates a Notification channel, for OREO and higher.
     */
    fun createNotificationChannel() {

        // Create a notification manager object.
        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            // Create the NotificationChannel with all the parameters.
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "Stand up notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Notifies every 15 minutes to " +
                    "stand up and walk"
            mNotificationManager!!.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        // Notification ID.
        private const val NOTIFICATION_ID = 0

        // Notification channel ID.
        private const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    }
}


class AlarmReceiver : BroadcastReceiver() {
    private var mNotificationManager: NotificationManager? = null

    /**
     * Called when the BroadcastReceiver receives an Intent broadcast.
     *
     * @param context The Context in which the receiver is running.
     * @param intent The Intent being received.
     */
    override fun onReceive(context: Context, intent: Intent) {
        mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Deliver the notification.
        deliverNotification(context)
    }

    /**
     * Builds and delivers the notification.
     *
     * @param context, activity context.
     */
    private fun deliverNotification(context: Context) {
        // Create the content intent for the notification, which launches
        // this activity
        val contentIntent = Intent(context, LoginActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            contentIntent,
            FLAG_UPDATE_CURRENT
        )
        // Build the notification
        val builder = NotificationCompat.Builder(
            context,
            PRIMARY_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.arms)
            .setContentTitle("context.getString(R.string.notification_title)")
            .setContentText("context.getString(R.string.notification_text)")
            .setContentIntent(contentPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        // Deliver the notification
        mNotificationManager!!.notify(
            NOTIFICATION_ID,
            builder.build()
        )
    }

    companion object {
        // Notification ID.
        private const val NOTIFICATION_ID = 0

        // Notification channel ID.
        private const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    }
}