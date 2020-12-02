package com.epmus.mobile.Messaging

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.epmus.mobile.R
import com.epmus.mobile.SettingsActivity
import com.epmus.mobile.ui.login.realmApp
import com.epmus.mobile.uiThreadRealmExercices
import com.epmus.mobile.uiThreadRealmUserId
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_messaging.*
import kotlinx.android.synthetic.main.user_row_message.view.*
import kotlin.system.exitProcess


class MessagingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messaging)

        setSupportActionBar(findViewById(R.id.toolbar_Messaging))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        fetchUsers()
    }

    companion object{
        val USER_KEY = "USER_KEY"
    }

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                p0.children.forEach { users ->
                    users.getValue(MessagingUser::class.java)?.let { user ->
                        adapter.add(UserItem(user, this@MessagingActivity))
                    }

                }

                adapter.setOnItemClickListener{ item, view->
                    val userItem = item as UserItem

                    val intent = Intent(view.context, ChatLogActivity::class.java)
                   // intent.putExtra(USER_KEY, userItem.user.nickname)
                    intent.putExtra(USER_KEY, userItem.user.nickname)
                    startActivity(intent)

                    finish()

                }

                recyclerview_newmessage.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
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


class UserItem(val user: MessagingUser, val context: Context) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int){
        viewHolder.itemView.username_textview_new_message.text = user.nickname

        // Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageview_new_message)
    }

    override fun getLayout(): Int {
        return R.layout.user_row_message
    }
}

