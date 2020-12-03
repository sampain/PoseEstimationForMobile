package com.epmus.mobile.Messaging

import android.content.Intent
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.epmus.mobile.Messaging.DateUtils.getFormattedTimeChatLog
import com.epmus.mobile.R
import com.epmus.mobile.SettingsActivity
import com.epmus.mobile.models.ChatMessage
import com.epmus.mobile.ui.login.realmApp
import com.epmus.mobile.uiThreadRealmExercices
import com.epmus.mobile.uiThreadRealmUserId
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = ChatLogActivity::class.java.simpleName
    }

    val adapter = GroupAdapter<ViewHolder>()

    private val toUser: MessagingUser
        get() = intent.getParcelableExtra(MessagingActivity.USER_KEY)!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_ChatLogMessaging)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        recyclerview_chat_log.adapter = adapter

        supportActionBar?.title = toUser.nickname

        listenForMessages()

        send_button_chat_log.setOnClickListener {
            performSendMessage()
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

    private fun listenForMessages() {

        val fromId = realmApp.currentUser()?.id
        val toId = toUser.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, "database error: " + databaseError.message)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "has children: " + dataSnapshot.hasChildren())
            }
        })

        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                dataSnapshot.getValue(ChatMessage::class.java)?.let {
                    if (it.fromId != realmApp.currentUser()?.id) {
                        adapter.add(ChatFromItem(it.message, it.timestamp))
                    } else {
                        adapter.add(ChatToItem(it.message, it.timestamp))
                    }
                }
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
            }

        })

    }

    private fun performSendMessage() {
        val text = Text_chat_log.text.toString()

        if (text.trim().isEmpty()) {
            return
        }

        val fromId = realmApp.currentUser()?.id
        val toId = toUser.uid

        val reference =
            FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference =
            FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage =
            ChatMessage(reference.key!!, text, fromId!!, toId, System.currentTimeMillis() / 1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Text_chat_log.text.clear()
                recyclerview_chat_log.smoothScrollToPosition(adapter.itemCount - 1)
            }

        toReference.setValue(chatMessage)
    }

}

class ChatFromItem(val text: String, val timestamp: Long) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.textView_from.text = text
        viewHolder.itemView.from_msg_time.text = getFormattedTimeChatLog(timestamp)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

}

class ChatToItem(val text: String, val timestamp: Long) :
    Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_to.text = text
        viewHolder.itemView.to_msg_time.text = getFormattedTimeChatLog(timestamp)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

}

object DateUtils {

    val fullFormattedTime = SimpleDateFormat("d MMM, h:mm a", Locale.US) // the format of your date
    private val onlyTime = SimpleDateFormat("h:mm a", Locale.US) // the format of your date
    private val onlyDate = SimpleDateFormat("d MMM", Locale.US) // the format of your date

    fun getFormattedTime(timeInMilis: Long): String {
        val date = Date(timeInMilis * 1000L) // *1000 is to convert seconds to milliseconds

        return when {
            isToday(date) -> onlyTime.format(date)
            isYesterday(date) -> "Yesterday"
            else -> onlyDate.format(date)
        }

    }

    fun getFormattedTimeChatLog(timeInMilis: Long): String {
        val date = Date(timeInMilis * 1000L) // *1000 is to convert seconds to milliseconds
        val fullFormattedTime =
            SimpleDateFormat("d MMM, h:mm a", Locale.US) // the format of your date
        val onlyTime = SimpleDateFormat("h:mm a", Locale.US) // the format of your date

        return when {
            isToday(date) -> onlyTime.format(date)
            else -> fullFormattedTime.format(date)
        }

    }

    fun isYesterday(d: Date): Boolean {
        return DateUtils.isToday(d.time + DateUtils.DAY_IN_MILLIS)
    }

    fun isToday(d: Date): Boolean {
        return DateUtils.isToday(d.time)
    }
}
