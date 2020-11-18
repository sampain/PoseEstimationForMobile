package com.epmus.mobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.epmus.mobile.program.*
import java.text.SimpleDateFormat
import java.util.*

class HistoryActivity : AppCompatActivity() {
    lateinit var historyList: ArrayList<HistoryData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_History)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //val findIterable : FindIterable<Document>? = mongoCollection?.find()

        /*findIterable?.iterator()?.getAsync {
            if (it.isSuccess) {
                historyList = ArrayList()
                it.get().forEach {
                    val historyData = HistoryData(it["name"].toString(), SimpleDateFormat(it["date"].toString()), it["time"].toString() )
                    historyList.add(historyData)
                }
                setupRecyclerView(findViewById(R.id.history_list))
            } else {
            }
        }*/
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(historyList)
    }

    class SimpleItemRecyclerViewAdapter(
        private val values: List<HistoryData>,
    ) :
        RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListenerDetails: View.OnClickListener

        init {
            onClickListenerDetails = View.OnClickListener { v ->
                val item = v.tag as ProgramContent.ProgramItem
                val intent = Intent(v.context, ProgramDetailActivity::class.java).apply {
                    putExtra(ProgramDetailFragment.ARG_ITEM_ID, item.id)
                }
                v.context.startActivity(intent)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.history_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]
            holder.idView.text = item.name
            holder.contentView.text = item.time

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListenerDetails)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val idView: TextView = view.findViewById(R.id.id_text_history)
            val contentView: TextView = view.findViewById(R.id.content_history)
        }
    }
}

data class HistoryData(
    var name: String,
    var date: SimpleDateFormat,
    var time: String
)