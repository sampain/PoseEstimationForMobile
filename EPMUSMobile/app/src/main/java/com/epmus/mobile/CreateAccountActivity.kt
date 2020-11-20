package com.epmus.mobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.epmus.mobile.program.ProgramListActivity
import com.epmus.mobile.ui.login.LoginActivity
import com.epmus.mobile.ui.login.realmApp
import kotlinx.android.synthetic.main.activity_login.*

class CreateAccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_Create)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val username = findViewById<EditText>(R.id.create_username)
        val password = findViewById<EditText>(R.id.create_password)
        val createAccoutButton = findViewById<Button>(R.id.createAccoutButton)

        createAccoutButton.setOnClickListener {
            createUser(username.toString(), password.toString())
        }
    }

    private fun createUser(username: String, password: String) {
        realmApp.emailPassword.registerUserAsync(username, password) {
            if (!it.isSuccess) {
                Toast.makeText(
                    baseContext,
                    it.error.message ?: "Mot de passe et/ou nom d'utilisateur invalide",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}