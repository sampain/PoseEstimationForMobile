package com.epmus.mobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.epmus.mobile.MongoDbService.historique
import com.epmus.mobile.poseestimation.ExerciceType

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

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(historic)
    }

    class SimpleItemRecyclerViewAdapter(
        private val values: List<historique>,
    ) :
        RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListenerDetails: View.OnClickListener

        init {
            onClickListenerDetails = View.OnClickListener { v ->

                val test = v.tag as LinearLayout
                if (test.visibility == View.VISIBLE) {
                    test.visibility = View.GONE;
                } else {
                    TransitionManager.beginDelayedTransition(
                        v as CardView,
                        AutoTransition()
                    );
                    test.visibility = View.VISIBLE
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
            holder.contentView.text = item.date
            holder.exerciceType.text = item.exerciceType
            holder.time.text = item.duree
            holder.nbr.text = item.nbrRepetitionOrHoldTime

            val exerciceTypeEnum = ExerciceType.getEnumValue(item.exerciceType)

            if (exerciceTypeEnum == ExerciceType.HOLD) {
                holder.nbrText.text = "Temps soutenu"
            }

            with(holder.itemView) {
                tag = holder.hidden_details
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
            val hidden_details: LinearLayout = view.findViewById(R.id.hidden_details)
        }
    }
}