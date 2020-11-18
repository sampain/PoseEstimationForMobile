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
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.kotlin.where
import io.realm.mongodb.User
import io.realm.mongodb.sync.SyncConfiguration
import org.bson.types.ObjectId

var statistics: MutableList<historique> = mutableListOf()
lateinit var uiThreadRealm: Realm

class MainMenuActivity : AppCompatActivity() {
    lateinit var historyLister: RealmResults<historique>
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
    }

    private fun addChangeListenerToRealm(realm: Realm) {
        historyLister = realm.where<historique>().findAllAsync()

        historyLister.addChangeListener { collection, changeSet ->

            statistics = realm.copyFromRealm(collection);

            if (historyView != null) {
                historyView!!.adapter = HistoryActivity.SimpleItemRecyclerViewAdapter(statistics)
            }

            // process deletions in reverse order if maintaining parallel data structures so indices don't change as you iterate
            /*val deletions = changeSet.deletionRanges
            for (i in deletions.indices.reversed()) {
                val range = deletions[i]
            }

            val insertions = changeSet.insertionRanges
            for (range in insertions) {
            }

            val modifications = changeSet.changeRanges
            for (range in modifications) {
            }*/
        }
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

enum class TaskStatus(val displayName: String) {
    Open("Open"),
    InProgress("In Progress"),
    Complete("Complete"),
}

open class historique(
    _patientId: String = "test",
    _programme_id: String = "Statistics",
    _date: String = "My Project",
    _duree: String = "defaultTime",
    _activite: RealmList<activite>? = null
) :
    RealmObject() {
    @PrimaryKey
    var _id: ObjectId = ObjectId()

    var patient_id = _patientId

    var date: String = _date

    var duree: String = _duree

    var programme_id: String = _programme_id

    var activite = _activite
}

@RealmClass(embedded = true)
open class activite(
    var nom: String? = null,
    var resultat: Int? = null,
    var repetition: Int? = null,
): RealmObject() {}