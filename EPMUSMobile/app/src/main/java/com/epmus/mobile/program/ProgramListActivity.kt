package com.epmus.mobile.program

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.epmus.mobile.*
import com.epmus.mobile.messaging.MessagingActivity
import com.epmus.mobile.mongodbservice.MongoTransactions
import com.epmus.mobile.poseestimation.CameraActivity
import com.epmus.mobile.poseestimation.ExerciseType
import com.epmus.mobile.poseestimation.ExerciseTypeUI
import com.epmus.mobile.ui.login.realmApp
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.DayOfWeek
import java.util.*
import kotlin.system.exitProcess

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [ProgramDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class ProgramListActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false
    private var notificationManager: NotificationManager? = null
    val monday: MutableList<String> = mutableListOf()
    val tuesday: MutableList<String> = mutableListOf()
    val wednesday: MutableList<String> = mutableListOf()
    val thursday: MutableList<String> = mutableListOf()
    val friday: MutableList<String> = mutableListOf()
    val saturday: MutableList<String> = mutableListOf()
    val sunday: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_program_list)

        //Set toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar_Program)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        findViewById<FloatingActionButton>(R.id.fab_messaging).setOnClickListener { view ->
            val intent = Intent(view.context, MessagingActivity::class.java)
            startActivity(intent)
        }

        if (findViewById<NestedScrollView>(R.id.program_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }

        //Fill exercise list
        setupRecyclerView(findViewById(R.id.program_list))
        getExerciseDay()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_actions, menu)
        return true
    }

    //Toolbar options
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            true
        }

        R.id.action_logout -> {
            realmApp.currentUser()?.logOutAsync {
                MongoTransactions.uiThreadRealmUserId.close()
                MongoTransactions.uiThreadRealmExercises.close()
                finishAffinity()
                exitProcess(1)
            }
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter =
            SimpleItemRecyclerViewAdapter(this, MongoTransactions.exerciseList, twoPane)
    }

    class SimpleItemRecyclerViewAdapter(
        private val parentActivity: ProgramListActivity,
        private val values: List<ExerciseData>,
        private val twoPane: Boolean
    ) :
        RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListenerDetails: View.OnClickListener
        private val onClickListenerPlay: View.OnClickListener


        init {
            onClickListenerPlay = View.OnClickListener { v ->
                val item = v.tag as ExerciseData
                val intent = Intent(v.context, CameraActivity::class.java)
                intent.putExtra("exercise", item)
                v.context.startActivity(intent)
            }

            onClickListenerDetails = View.OnClickListener { v ->
                val item = v.tag as ExerciseData
                if (twoPane) {
                    val fragment = ProgramDetailFragment().apply {
                        arguments = Bundle().apply {
                            putString(ProgramDetailFragment.ARG_ITEM_ID, item.id)
                        }
                    }
                    parentActivity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.program_detail_container, fragment)
                        .commit()
                } else {
                    val intent = Intent(v.context, ProgramDetailActivity::class.java).apply {
                        putExtra(ProgramDetailFragment.ARG_ITEM_ID, item)
                    }
                    v.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.program_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]
            holder.idView.text = item.name
            holder.contentView.text =
                ExerciseTypeUI.getEnumValue(item.exercise.exerciseType.toString()).toString()

            when (item.exercise.exerciseType) {
                ExerciseType.HOLD -> {
                    holder.contentView.setTextColor(Color.parseColor("#66BB6A"))
                }
                ExerciseType.REPETITION -> {
                    holder.contentView.setTextColor(Color.parseColor("#FF9800"))
                }
                ExerciseType.CHRONO -> {
                    holder.contentView.setTextColor(Color.parseColor("#29B6F6"))
                }
                ExerciseType.AMPLITUDE -> {
                    holder.contentView.setTextColor(Color.parseColor("#774C55"))
                }
            }

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListenerDetails)
            }

            with(holder.imageButton) {
                tag = item
                setOnClickListener(onClickListenerPlay)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val idView: TextView = view.findViewById(R.id.id_text)
            val contentView: TextView = view.findViewById(R.id.content)
            val imageButton: ImageButton = view.findViewById(R.id.playButton)
        }
    }

    private fun createAlarms() {
        val repeatInterval = AlarmManager.INTERVAL_DAY * 7

        // 12h00
        val hourOfDay = 12
        val minuteOfDay = 0

        val calendarMonday: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minuteOfDay)
            set(Calendar.DAY_OF_WEEK, DayOfWeek.MONDAY.ordinal)
        }
        val calendarTuesday: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minuteOfDay)
            set(Calendar.DAY_OF_WEEK, DayOfWeek.TUESDAY.ordinal)
        }
        val calendarWednesday: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minuteOfDay)
            set(Calendar.DAY_OF_WEEK, DayOfWeek.WEDNESDAY.ordinal)
        }
        val calendarThursday: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minuteOfDay)
            set(Calendar.DAY_OF_WEEK, DayOfWeek.THURSDAY.ordinal)
        }
        val calendarFriday: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minuteOfDay)
            set(Calendar.DAY_OF_WEEK, DayOfWeek.FRIDAY.ordinal)
        }
        val calendarSaturday: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minuteOfDay)
            set(Calendar.DAY_OF_WEEK, DayOfWeek.SATURDAY.ordinal)
        }
        val calendarSunday: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minuteOfDay)
            set(Calendar.DAY_OF_WEEK, DayOfWeek.SUNDAY.ordinal)
        }


        //Acces to saved preference (alarm on or off)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager!!.cancelAll()

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        val intent = Intent(this, AlarmReceiver::class.java)


        // Verify if an alarm is up
        val mondayUp = (PendingIntent.getBroadcast(
            this,
            NOTIFICATION_ID_MONDAY,
            intent,
            PendingIntent.FLAG_NO_CREATE
        ) != null)

        val tuesdayUp = (PendingIntent.getBroadcast(
            this,
            NOTIFICATION_ID_TUESDAY,
            intent,
            PendingIntent.FLAG_NO_CREATE
        ) != null)

        val wednesdayUp = (PendingIntent.getBroadcast(
            this,
            NOTIFICATION_ID_WEDNESDAY,
            intent,
            PendingIntent.FLAG_NO_CREATE
        ) != null)

        val thursdayUp = (PendingIntent.getBroadcast(
            this,
            NOTIFICATION_ID_THURSDAY,
            intent,
            PendingIntent.FLAG_NO_CREATE
        ) != null)

        val fridayUp = (PendingIntent.getBroadcast(
            this,
            NOTIFICATION_ID_FRIDAY,
            intent,
            PendingIntent.FLAG_NO_CREATE
        ) != null)

        val saturdayUp = (PendingIntent.getBroadcast(
            this,
            NOTIFICATION_ID_SATURDAY,
            intent,
            PendingIntent.FLAG_NO_CREATE
        ) != null)

        val sundayUp = (PendingIntent.getBroadcast(
            this,
            NOTIFICATION_ID_SUNDAY,
            intent,
            PendingIntent.FLAG_NO_CREATE
        ) != null)


        //Create alarm intent
        val intentMonday =
            PendingIntent.getBroadcast(
                this,
                NOTIFICATION_ID_MONDAY,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        val intentTuesday =
            PendingIntent.getBroadcast(
                this,
                NOTIFICATION_ID_TUESDAY,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        val intentWednesday =
            PendingIntent.getBroadcast(
                this,
                NOTIFICATION_ID_WEDNESDAY,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        val intentThursday =
            PendingIntent.getBroadcast(
                this,
                NOTIFICATION_ID_THURSDAY,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        val intentFriday =
            PendingIntent.getBroadcast(
                this,
                NOTIFICATION_ID_FRIDAY,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        val intentSaturday =
            PendingIntent.getBroadcast(
                this,
                NOTIFICATION_ID_SATURDAY,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        val intentSunday =
            PendingIntent.getBroadcast(
                this,
                NOTIFICATION_ID_SUNDAY,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )


        //Create alarm
        if (monday.isNotEmpty() && !sharedPreferences?.getBoolean("alarms_setting", false)!!) {
            if (!mondayUp) {
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendarMonday.timeInMillis, repeatInterval,
                    intentMonday
                )
            }
        } else {
            alarmManager.cancel(intentMonday)
        }

        if (tuesday.isNotEmpty() && !sharedPreferences?.getBoolean("alarms_setting", false)!!) {
            if (!tuesdayUp) {
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendarTuesday.timeInMillis, repeatInterval,
                    intentTuesday
                )
            }

        } else {
            alarmManager.cancel(intentTuesday)
        }

        if (wednesday.isNotEmpty() && !sharedPreferences?.getBoolean("alarms_setting", false)!!) {
            if (!wednesdayUp) {
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendarWednesday.timeInMillis, repeatInterval,
                    intentWednesday
                )
            }
        } else {
            alarmManager.cancel(intentWednesday)
        }

        if (thursday.isNotEmpty() && !sharedPreferences?.getBoolean("alarms_setting", false)!!) {
            if (!thursdayUp) {
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendarThursday.timeInMillis, repeatInterval,
                    intentThursday
                )
            }

        } else {
            alarmManager.cancel(intentThursday)
        }

        if (friday.isNotEmpty() && !sharedPreferences?.getBoolean("alarms_setting", false)!!) {
            if (!fridayUp) {
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendarFriday.timeInMillis, repeatInterval,
                    intentFriday
                )
            }

        } else {
            alarmManager.cancel(intentFriday)
        }

        if (saturday.isNotEmpty() && !sharedPreferences?.getBoolean("alarms_setting", false)!!) {
            if (!saturdayUp) {
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendarSaturday.timeInMillis, repeatInterval,
                    intentSaturday
                )
            }

        } else {
            alarmManager.cancel(intentSaturday)
        }

        if (sunday.isNotEmpty() && !sharedPreferences?.getBoolean("alarms_setting", false)!!) {
            if (!sundayUp) {
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendarSunday.timeInMillis, repeatInterval,
                    intentSunday
                )
            }
        } else {
            alarmManager.cancel(intentSunday)
        }

        //Create notification if alarm is not disabled
        if (!sharedPreferences?.getBoolean("alarms_setting", false)!!) {
            createNotificationChannel()
        }
    }

    private fun getExerciseDay() {
        MongoTransactions.exerciseList.forEach { exerciseData ->
            if (exerciseData.mondayAlarm) {
                monday.add(exerciseData.name)
            }
            if (exerciseData.tuesdayAlarm) {
                tuesday.add(exerciseData.name)
            }
            if (exerciseData.wednesdayAlarm) {
                wednesday.add(exerciseData.name)
            }
            if (exerciseData.thursdayAlarm) {
                thursday.add(exerciseData.name)
            }
            if (exerciseData.fridayAlarm) {
                friday.add(exerciseData.name)
            }
            if (exerciseData.saturdayAlarm) {
                saturday.add(exerciseData.name)
            }
            if (exerciseData.sundayAlarm) {
                sunday.add(exerciseData.name)
            }
        }
        createAlarms()
    }

    private fun createNotificationChannel() {

        // Create a notification manager object.
        notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Create the NotificationChannel with all the parameters.
        val notificationChannel = NotificationChannel(
            PRIMARY_CHANNEL_ID,
            "Motification",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)

        notificationManager!!.createNotificationChannel(notificationChannel)
    }

    companion object {
        private const val PRIMARY_CHANNEL_ID = "primary_notification_channel"

        // Notification ID.
        private const val NOTIFICATION_ID_MONDAY = 0
        private const val NOTIFICATION_ID_TUESDAY = 1
        private const val NOTIFICATION_ID_WEDNESDAY = 2
        private const val NOTIFICATION_ID_THURSDAY = 3
        private const val NOTIFICATION_ID_FRIDAY = 4
        private const val NOTIFICATION_ID_SATURDAY = 5
        private const val NOTIFICATION_ID_SUNDAY = 6
    }
}