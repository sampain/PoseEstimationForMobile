package com.epmus.mobile

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.epmus.mobile.Messaging.NewMessageActivity
import com.epmus.mobile.MongoDbService.MongoTransactions
import com.epmus.mobile.MongoDbService.historique
import com.epmus.mobile.program.ExerciceData
import com.epmus.mobile.program.ProgramListActivity
import com.epmus.mobile.ui.login.LoginActivity
import com.epmus.mobile.ui.login.realmApp
import io.realm.Realm

var historic: MutableList<historique> = mutableListOf()
var globalExerciceList: MutableList<ExerciceData> = mutableListOf()
lateinit var uiThreadRealmUserId: Realm
lateinit var uiThreadRealmTempId: Realm
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
            val intent = Intent(this, NewMessageActivity::class.java)
            startActivity(intent)
        }

        alerts.setOnClickListener {
            val intent = Intent(this, AlertsActivity::class.java)
            startActivity(intent)
        }

        //Add listener to Realm
        uiThreadRealmUserId = Realm.getInstance(MongoTransactions.configUserId)
        uiThreadRealmTempId = Realm.getInstance(MongoTransactions.configTempId)
        uiThreadRealmExercices = Realm.getInstance(MongoTransactions.configExercices)
        MongoTransactions.addChangeListenerToRealm(
            uiThreadRealmUserId,
            uiThreadRealmTempId,
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