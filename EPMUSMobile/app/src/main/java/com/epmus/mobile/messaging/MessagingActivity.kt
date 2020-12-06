package com.epmus.mobile.messaging

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epmus.mobile.*
import com.epmus.mobile.mongodbservice.MongoTransactions
import com.epmus.mobile.R
import com.epmus.mobile.mongodbservice.MongoTransactions.Companion.physioList
import com.epmus.mobile.ui.login.realmApp
import kotlin.system.exitProcess


class MessagingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messaging)

        setSupportActionBar(findViewById(R.id.toolbar_Messaging))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupRecyclerView(findViewById(R.id.recyclerview_newmessage), physioList)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_actions, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }

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

    private fun setupRecyclerView(recyclerView: RecyclerView, users: MutableList<MessagingUser>) {
        recyclerView.adapter =
            SimpleItemRecyclerViewAdapter(users)
    }

    class SimpleItemRecyclerViewAdapter(
        private val values: MutableList<MessagingUser>
    ) :
        RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListener: View.OnClickListener

        init {
            onClickListener = View.OnClickListener { v ->
                val item = v.tag as MessagingUser
                val intent = Intent(v.context, ChatLogActivity::class.java).apply {
                    putExtra(USER_KEY, item)
                }
                v.context.startActivity(intent)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.user_row_message, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]
            holder.idView.text = item.nickname

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val idView: TextView = view.findViewById(R.id.username_textview_new_message)
        }
    }

    companion object {
        const val USER_KEY = "USER_KEY"
    }
}

