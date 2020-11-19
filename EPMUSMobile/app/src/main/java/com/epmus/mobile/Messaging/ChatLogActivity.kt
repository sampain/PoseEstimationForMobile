package com.epmus.mobile.Messaging

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.epmus.mobile.R
import com.epmus.mobile.SettingsActivity
import com.epmus.mobile.ui.login.LoginActivity
import com.epmus.mobile.ui.login.realmApp
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.user_row_message.view.*

class ChatLogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        supportActionBar?.title = "Chat Log"

        val adapter = GroupAdapter<ViewHolder>()

        adapter.add(ChatFItem())
        adapter.add(ChatTItem())
        adapter.add(ChatFItem())
        adapter.add(ChatTItem())
        adapter.add(ChatFItem())
        adapter.add(ChatTItem())
        adapter.add(ChatFItem())
        adapter.add(ChatTItem())
        adapter.add(ChatFItem())
        adapter.add(ChatTItem())
        adapter.add(ChatFItem())
        adapter.add(ChatTItem())

        recyclerview_chat_log.adapter = adapter
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

class ChatFItem: Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int){

    }

    override fun getLayout(): Int {
        return R.layout.chat_f_row
    }
}

class ChatTItem: Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int){

    }

    override fun getLayout(): Int {
        return R.layout.chat_t_row
    }
}
