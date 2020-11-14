package com.epmus.mobile

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import com.epmus.mobile.Messaging.MessagingActivity
import androidx.appcompat.app.AppCompatActivity
import com.epmus.mobile.Messaging.NewMessageActivity
import com.epmus.mobile.MongoDbService.MongoTransactions
import com.epmus.mobile.program.ExerciceData
import com.epmus.mobile.program.ProgramListActivity
import com.epmus.mobile.ui.login.realmApp
import io.realm.Realm
import kotlin.system.exitProcess

var historic: MutableList<HistoryData> = mutableListOf()
var globalExerciceList: MutableList<ExerciceData> = mutableListOf()
lateinit var uiThreadRealmUserId: Realm
lateinit var uiThreadRealmExercices: Realm

class MainMenuActivity : AppCompatActivity() {

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
            val intent = Intent(this, MessagingActivity::class.java)
            startActivity(intent)
        }

        alerts.setOnClickListener {
            val intent = Intent(this, AlertsActivity::class.java)
            startActivity(intent)
        }

        //Add listener to Realm
        uiThreadRealmUserId = Realm.getInstance(MongoTransactions.configUserId)
        uiThreadRealmExercices = Realm.getInstance(MongoTransactions.configExercices)
        MongoTransactions.addChangeListenerToRealm(
            uiThreadRealmUserId,
            uiThreadRealmExercices
        )
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
                uiThreadRealmUserId.close()
                uiThreadRealmExercices.close()
                finishAffinity()
                exitProcess(1)
            }
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}