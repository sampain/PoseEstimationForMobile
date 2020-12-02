package com.epmus.mobile.Messaging

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.epmus.mobile.models.ChatMessage
import com.epmus.mobile.R
import com.epmus.mobile.SettingsActivity
import com.epmus.mobile.ui.login.realmApp
import com.epmus.mobile.uiThreadRealmExercices
import com.epmus.mobile.uiThreadRealmUserId
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlin.system.exitProcess

class ChatLogActivity : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_ChatLogMessaging)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerview_chat_log.adapter = adapter

        val username = intent.getStringExtra(MessagingActivity.USER_KEY)
        supportActionBar?.title = username

        send_button_chat_log.setOnClickListener {
            performSendMessage()
        }

        listenForMessages()
    }

    private fun listenForMessages() {
        val ref = FirebaseDatabase.getInstance().getReference("/chats")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue((ChatMessage::class.java))

                if (chatMessage?.fromId != "Mobile") {
                    adapter.add(ChatFItem(chatMessage?.message.toString()))
                } else {
                    adapter.add(ChatTItem(chatMessage.message))
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun performSendMessage() {

        val message = Text_chat_log.text.toString()

        val fromId = "Mobile"

        val username = intent.getStringExtra(MessagingActivity.USER_KEY)

        val reference = FirebaseDatabase.getInstance().getReference("/chats").push()

        val chatMessage = username?.let {
            ChatMessage(
                reference.key!!,
                message,
                fromId,
                it,
                System.currentTimeMillis() / 1000
            )
        }
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Text_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
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

class ChatFItem(val text: String) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_from.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatTItem(val text: String) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_to.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}
