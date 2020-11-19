package com.epmus.mobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.Toolbar

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
    }
}