package com.epmus.mobile

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.epmus.mobile.Messaging.NewMessageActivity
import com.epmus.mobile.program.ProgramListActivity
import com.epmus.mobile.ui.login.LoginActivity
import com.epmus.mobile.ui.login.realmApp
import io.realm.OrderedRealmCollectionChangeListener
import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import io.realm.kotlin.where
import io.realm.mongodb.User
import io.realm.mongodb.sync.SyncConfiguration
import org.bson.types.ObjectId
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.FutureTask

class MainMenuActivity : AppCompatActivity() {
    lateinit var uiThreadRealm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        // toolbar support
        setSupportActionBar(findViewById(R.id.toolbar_MainMenu))

        val program = findViewById<Button>(R.id.activity_program)
        val statistics = findViewById<Button>(R.id.activity_statistics)
        val messaging = findViewById<Button>(R.id.activity_messaging)
        val alerts = findViewById<Button>(R.id.activity_alerts)

        program.setOnClickListener {
            val intent = Intent(this, ProgramListActivity::class.java)
            startActivity(intent)
        }

        statistics.setOnClickListener {
            val intent = Intent(this, StatisticsActivity::class.java)
            startActivity(intent)
        }

        messaging.setOnClickListener {
            val intent = Intent(this, NewMessageActivity::class.java)
            startActivity(intent)
        }

        alerts.setOnClickListener {
            val intent = Intent(this, AlertsActivity::class.java)
            startActivity(intent)
        }
        val user: User? = realmApp.currentUser()

        val partitionValue: String = "My Project"
        val config = SyncConfiguration.Builder(user, partitionValue)
            .build()

        uiThreadRealm = Realm.getInstance(config)
        addChangeListenerToRealm(uiThreadRealm)

        val task : FutureTask<String> = FutureTask(BackgroundQuickStart(realmApp.currentUser()!!), "test")
        val executorService: ExecutorService = Executors.newFixedThreadPool(2)
        executorService.execute(task)
    }

    fun addChangeListenerToRealm(realm: Realm) {
        // all tasks in the realm
        val tasks: RealmResults<Task> = realm.where<Task>().findAllAsync()

        tasks.addChangeListener(OrderedRealmCollectionChangeListener<RealmResults<Task>> { collection, changeSet ->
            // process deletions in reverse order if maintaining parallel data structures so indices don't change as you iterate
            val deletions = changeSet.deletionRanges
            for (i in deletions.indices.reversed()) {
                val range = deletions[i]
            }

            val insertions = changeSet.insertionRanges
            for (range in insertions) {
            }

            val modifications = changeSet.changeRanges
            for (range in modifications) {
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_actions, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            true
        }

        R.id.action_logout -> {
            realmApp.currentUser()?.logOutAsync {
                if (it.isSuccess) {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}

class BackgroundQuickStart(val user: User) : Runnable {

    override fun run() {
        val partitionValue: String = "My Project"
        val config = SyncConfiguration.Builder(user, partitionValue)
            .build()

        val backgroundThreadRealm: Realm = Realm.getInstance(config)

        val task: Task = Task("New Task", partitionValue)
        backgroundThreadRealm.executeTransaction { transactionRealm ->
            transactionRealm.insert(task)
        }

        // all tasks in the realm
        val tasks: RealmResults<Task> = backgroundThreadRealm.where<Task>().findAll()

        // you can also filter a collection
        val tasksThatBeginWithN: List<Task> = tasks.where().beginsWith("name", "N").findAll()
        val openTasks: List<Task> = tasks.where().equalTo("status", TaskStatus.Open.name).findAll()

        val otherTask: Task = tasks[0]!!

        // all modifications to a realm must happen inside of a write block
        backgroundThreadRealm.executeTransaction { transactionRealm ->
            val innerOtherTask: Task =
                transactionRealm.where<Task>().equalTo("_id", otherTask._id).findFirst()!!
            innerOtherTask.status = TaskStatus.Complete.name
        }

        val yetAnotherTask: Task = tasks.get(0)!!
        val yetAnotherTaskId: ObjectId = yetAnotherTask._id
        // all modifications to a realm must happen inside of a write block
        backgroundThreadRealm.executeTransaction { transactionRealm ->
            val innerYetAnotherTask: Task =
                transactionRealm.where<Task>().equalTo("_id", yetAnotherTaskId).findFirst()!!
            innerYetAnotherTask.deleteFromRealm()
        }

        // because this background thread uses synchronous realm transactions, at this point all
        // transactions have completed and we can safely close the realm
        backgroundThreadRealm.close()
    }

}

enum class TaskStatus(val displayName: String) {
    Open("Open"),
    InProgress("In Progress"),
    Complete("Complete"),
}

open class Task(_name: String = "Task", project: String = "My Project") : RealmObject() {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var name: String = _name

    @Required
    var status: String = TaskStatus.Open.name
    var statusEnum: TaskStatus
        get() {
            // because status is actually a String and another client could assign an invalid value,
            // default the status to "Open" if the status is unreadable
            return try {
                TaskStatus.valueOf(status)
            } catch (e: IllegalArgumentException) {
                TaskStatus.Open
            }
        }
        set(value) {
            status = value.name
        }
}