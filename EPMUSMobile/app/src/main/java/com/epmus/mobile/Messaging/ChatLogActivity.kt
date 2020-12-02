package com.epmus.mobile.Messaging

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.anshdeep.kotlinmessenger.models.ChatMessage
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
import kotlinx.android.synthetic.main.chat_f_row.view.*
import kotlin.system.exitProcess

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter = adapter
        val toolbar = findViewById<Toolbar>(R.id.toolbar_ChatLogMessaging)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


       val username = intent.getStringExtra(MessagingActivity.USER_KEY)
        //val user = intent.getParcelableExtra<MessagingUser>(MessagingActivity.USER_KEY)

        supportActionBar?.title = username

        //setupDummyData()
        ListenForMessages()

        envoyer_button_chat_log.setOnClickListener {
            Log.d(TAG, "Attempt to send message ....")
            performSendMessage()
        }
    }

    private fun ListenForMessages() {
        val fromId = "Mobile"

        val username = intent.getStringExtra(MessagingActivity.USER_KEY)
        val toId = username

        val ref = FirebaseDatabase.getInstance().getReference("/chats")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1:String?){
                val chatMessage = p0.getValue((ChatMessage::class.java))
                Log.d(TAG, chatMessage?.message.toString())

                if(chatMessage?.fromId == "Mobile"){
                    adapter.add(ChatFItem(chatMessage?.message.toString()))
                }

                else{
                    adapter.add(ChatTItem(chatMessage?.message.toString()))
                }

            }

            override fun onCancelled(p0: DatabaseError){

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

    private fun performSendMessage(){

        val message = Text_chat_log.text.toString()

        //val fromId = FirebaseAuth.getInstance().uid ?: return
        val fromId = "Mobile"

        val username = intent.getStringExtra(MessagingActivity.USER_KEY)
        val toId = username

        //val reference = FirebaseDatabase.getInstance().getReference("/chats").push()
        val reference = FirebaseDatabase.getInstance().getReference("/chats").push()

       // val toReference = FirebaseDatabase.getInstance().getReference("/chats").push()

        val chatMessage = toId?.let { ChatMessage(reference.key!!, message, fromId, it, System.currentTimeMillis()/1000) }
        reference.setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d(TAG, "Saved our chat message: ${reference.key}")
                    Text_chat_log.text.clear()
                    recyclerview_chat_log.scrollToPosition(adapter.itemCount -1)
                }
       // toReference.setValue(chatMessage)
    }

    private fun setupDummyData(){

        val adapter = GroupAdapter<ViewHolder>()

        adapter.add(ChatFItem("From Message...."))
        adapter.add(ChatTItem("To Message \n To Message"))

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

class ChatFItem(val text: String): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int){
        viewHolder.itemView.textView.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_f_row
    }
}

class ChatTItem(val text: String): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int){
       viewHolder.itemView.textView.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_t_row
    }
}
