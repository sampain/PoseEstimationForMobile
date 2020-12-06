package com.epmus.mobile

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import com.epmus.mobile.messaging.MessagingActivity
import androidx.appcompat.app.AppCompatActivity
import com.epmus.mobile.mongodbservice.MongoTransactions
import com.epmus.mobile.program.ProgramListActivity
import com.epmus.mobile.ui.login.realmApp
import kotlin.system.exitProcess

class MainMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        //Set toolbar
        setSupportActionBar(findViewById(R.id.toolbar_MainMenu))

        val program = findViewById<Button>(R.id.activity_program)
        val statistics = findViewById<Button>(R.id.activity_statistics)
        val messaging = findViewById<Button>(R.id.activity_messaging)

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

        //Initialize MongoTransactions
        MongoTransactions
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
}