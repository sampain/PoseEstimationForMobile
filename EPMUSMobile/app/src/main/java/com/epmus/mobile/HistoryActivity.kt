package com.epmus.mobile

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.epmus.mobile.mongodbservice.MongoTransactions
import com.epmus.mobile.poseestimation.ExerciseType
import com.epmus.mobile.poseestimation.ExerciseTypeUI
import com.epmus.mobile.ui.login.realmApp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess

var historyView: RecyclerView? = null

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        //Set toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar_History)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        historyView = findViewById(R.id.history_list)
        setupRecyclerView(findViewById(R.id.history_list))
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

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        //Set exercise history list
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(MongoTransactions.exerciseHistory)
    }

    class SimpleItemRecyclerViewAdapter(
        private val values: List<HistoryData>,
    ) :
        RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListenerDetails: View.OnClickListener

        init {
            onClickListenerDetails = View.OnClickListener { v ->

                val moreDetails = v.tag as LinearLayout
                if (moreDetails.visibility == View.VISIBLE) {
                    moreDetails.visibility = View.GONE
                } else {
                    TransitionManager.beginDelayedTransition(
                        v as CardView,
                        AutoTransition()
                    )
                    moreDetails.visibility = View.VISIBLE
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.history_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]
            holder.idView.text = item.exerciseName
            holder.contentView.text = item.date.toString()
            holder.exerciseType.text = ExerciseTypeUI.getEnumValue(item.exerciseType).toString()
            holder.time.text = item.duree
            holder.nbr.text = item.nbrRepetitionOrHoldTime

            val formatterTo = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            holder.contentView.text = item.date?.format(formatterTo) ?: ""

            when (ExerciseType.getEnumValue(item.exerciseType)) {
                ExerciseType.HOLD -> {
                    holder.nbrText.text = "Temps soutenu"
                    holder.idView.setTextColor(Color.parseColor("#66BB6A"))
                    holder.exerciseType.setTextColor(Color.parseColor("#66BB6A"))
                }
                ExerciseType.REPETITION -> {
                    holder.nbrText.text = "Nombre de répétitions"
                    holder.idView.setTextColor(Color.parseColor("#FF9800"))
                    holder.exerciseType.setTextColor(Color.parseColor("#FF9800"))
                }
                ExerciseType.CHRONO -> {
                    holder.nbrText.text = "Nombre de répétitions"
                    holder.idView.setTextColor(Color.parseColor("#29B6F6"))
                    holder.exerciseType.setTextColor(Color.parseColor("#29B6F6"))
                }
                ExerciseType.AMPLITUDE -> {
                    holder.nbrText.text = "Angle maximal (°)"
                    holder.idView.setTextColor(Color.parseColor("#774C55"))
                    holder.exerciseType.setTextColor(Color.parseColor("#774C55"))
                }
            }

            with(holder.itemView) {
                tag = holder.hiddenDetails
                setOnClickListener(onClickListenerDetails)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val idView: TextView = view.findViewById(R.id.id_text_history)
            val contentView: TextView = view.findViewById(R.id.content_history)
            val exerciseType: TextView = view.findViewById(R.id.history_type)
            val time: TextView = view.findViewById(R.id.history_time)
            val nbr: TextView = view.findViewById(R.id.history_nbr)
            val nbrText: TextView = view.findViewById(R.id.history_nbr_text)
            val hiddenDetails: LinearLayout = view.findViewById(R.id.hidden_details)
        }
    }
}

class HistoryData(
    _exerciseName: String = "",
    _exerciseType: String = "",
    _date: LocalDateTime? = null,
    _duree: String = "",
    _nbrRepetitionOrHoldTime: String = ""
) {
    var date = _date

    var duree = _duree

    var exerciseName = _exerciseName

    var exerciseType = _exerciseType

    var nbrRepetitionOrHoldTime = _nbrRepetitionOrHoldTime
}
