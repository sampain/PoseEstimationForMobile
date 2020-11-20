package com.epmus.mobile.program

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import androidx.core.widget.NestedScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.TextView
import com.epmus.mobile.Messaging.NewMessageActivity
import com.epmus.mobile.R
import com.epmus.mobile.SettingsActivity
import com.epmus.mobile.globalExerciceList
import com.epmus.mobile.poseestimation.CameraActivity
import com.epmus.mobile.ui.login.LoginActivity
import com.epmus.mobile.ui.login.realmApp

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [ProgramDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class ProgramListActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_program_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_Program)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        findViewById<FloatingActionButton>(R.id.fab_messaging).setOnClickListener { view ->
            val intent = Intent(view.context, NewMessageActivity::class.java)
            startActivity(intent)
        }

        if (findViewById<NestedScrollView>(R.id.program_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }

        setupRecyclerView(findViewById(R.id.program_list))
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

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter =
            SimpleItemRecyclerViewAdapter(this, globalExerciceList, twoPane)
    }

    class SimpleItemRecyclerViewAdapter(
        private val parentActivity: ProgramListActivity,
        private val values: List<ExerciceData>,
        private val twoPane: Boolean
    ) :
        RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListenerDetails: View.OnClickListener
        private val onClickListenerPlay: View.OnClickListener


        init {
            onClickListenerPlay = View.OnClickListener { v ->
                val item = v.tag as ExerciceData
                val intent = Intent(v.context, CameraActivity::class.java)
                /*val program = ProgramContent.ITEM_MAP[item.id]
                val exerciceData = ExerciceData()
                val exerciceDataPopulated =
                    exerciceData.getExerciceData(ExerciceNameList.getEnumValue(program!!.content))*/
                intent.putExtra("exercice", item)
                v.context.startActivity(intent)
            }

            onClickListenerDetails = View.OnClickListener { v ->
                val item = v.tag as ProgramContent.ProgramItem
                if (twoPane) {
                    val fragment = ProgramDetailFragment().apply {
                        arguments = Bundle().apply {
                            putString(ProgramDetailFragment.ARG_ITEM_ID, item.id)
                        }
                    }
                    parentActivity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.program_detail_container, fragment)
                        .commit()
                } else {
                    val intent = Intent(v.context, ProgramDetailActivity::class.java).apply {
                        putExtra(ProgramDetailFragment.ARG_ITEM_ID, item.id)
                    }
                    v.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.program_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]
            holder.idView.text = "1"
            holder.contentView.text = item.name

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListenerDetails)
            }

            with(holder.imageButton) {
                tag = item
                setOnClickListener(onClickListenerPlay)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val idView: TextView = view.findViewById(R.id.id_text)
            val contentView: TextView = view.findViewById(R.id.content)
            val imageButton: ImageButton = view.findViewById(R.id.playButton)
        }
    }
}