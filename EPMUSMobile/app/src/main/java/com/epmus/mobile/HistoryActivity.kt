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
import com.epmus.mobile.poseestimation.ExerciceType
import com.epmus.mobile.poseestimation.ExerciceTypeUI
import com.epmus.mobile.ui.login.realmApp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess

var historyView: RecyclerView? = null

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

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

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(historic)
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
            holder.idView.text = item.exerciceName
            holder.contentView.text = item.date.toString()
            holder.exerciceType.text = ExerciceTypeUI.getEnumValue(item.exerciceType).toString()
            holder.time.text = item.duree
            holder.nbr.text = item.nbrRepetitionOrHoldTime

            val formatterTo = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            holder.contentView.text = item.date?.format(formatterTo) ?: ""

            val exerciceTypeEnum = ExerciceType.getEnumValue(item.exerciceType)

            if (exerciceTypeEnum == ExerciceType.HOLD) {
                holder.nbrText.text = "Temps soutenu"
            }

            when (ExerciceType.getEnumValue(item.exerciceType)) {
                ExerciceType.HOLD -> {
                    holder.idView.setTextColor(Color.parseColor("#66BB6A"))
                    holder.exerciceType.setTextColor(Color.parseColor("#66BB6A"))
                }
                ExerciceType.REPETITION -> {
                    holder.idView.setTextColor(Color.parseColor("#FF9800"))
                    holder.exerciceType.setTextColor(Color.parseColor("#FF9800"))
                }
                ExerciceType.CHRONO -> {
                    holder.idView.setTextColor(Color.parseColor("#29B6F6"))
                    holder.exerciceType.setTextColor(Color.parseColor("#29B6F6"))
                }
                ExerciceType.AMPLITUDE -> {
                    holder.idView.setTextColor(Color.parseColor("#774C55"))
                    holder.exerciceType.setTextColor(Color.parseColor("#774C55"))
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
            val exerciceType: TextView = view.findViewById(R.id.history_type)
            val time: TextView = view.findViewById(R.id.history_time)
            val nbr: TextView = view.findViewById(R.id.history_nbr)
            val nbrText: TextView = view.findViewById(R.id.history_nbr_text)
            val hiddenDetails: LinearLayout = view.findViewById(R.id.hidden_details)
        }
    }
}

class HistoryData(
    _exerciceName: String = "",
    _exerciceType: String = "",
    _date: LocalDateTime? = null,
    _duree: String = "",
    _nbrRepetitionOrHoldTime: String = ""
) {
    var date = _date

    var duree = _duree

    var exerciceName = _exerciceName

    var exerciceType = _exerciceType

    var nbrRepetitionOrHoldTime = _nbrRepetitionOrHoldTime
}
