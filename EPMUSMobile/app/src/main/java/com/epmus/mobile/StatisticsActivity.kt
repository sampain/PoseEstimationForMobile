package com.epmus.mobile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.epmus.mobile.poseestimation.ExerciceType
import com.epmus.mobile.ui.login.LoginActivity
import com.epmus.mobile.ui.login.realmApp
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel

class StatisticsActivity : AppCompatActivity() {
    lateinit var statistic_count: TextView
    lateinit var statistic_count_7: TextView
    lateinit var pieChart: PieChart
    lateinit var history: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_Statistics)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        statistic_count = findViewById(R.id.statistic_count);
        statistic_count_7 = findViewById(R.id.statistic_count_7);
        pieChart = findViewById(R.id.piechart);
        history  = findViewById(R.id.history_number);

        val history = findViewById<CardView>(R.id.history_button);

        history.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }


        setData()
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

    private fun setData() {
        statistic_count.setText(statistics.count().toString())

        var holdCount = 0
        var repetitionCount = 0
        var chronoCount = 0

        statistics.forEach{
            val exerciceType = ExerciceType.getEnumValue(it.exerciceType)
            if(exerciceType == ExerciceType.HOLD){
                holdCount++
            }
            else if(exerciceType == ExerciceType.REPETITION){
                repetitionCount++
            }
            else{
                chronoCount++
            }
        }

        statistic_count_7.setText(999999.toString())

        history.setText(statistics.count().toString())

        pieChart.addPieSlice(
            PieModel(
                "Type HOLD", holdCount.toFloat(),
                Color.parseColor("#66BB6A")
            )
        )
        pieChart.addPieSlice(
            PieModel(
                "Type REPETITION", repetitionCount.toFloat(),
                Color.parseColor("#EF5350")
            )
        )
        pieChart.addPieSlice(
            PieModel(
                "Type CHRONO", chronoCount.toFloat(),
                Color.parseColor("#29B6F6")
            )
        )

        pieChart.startAnimation()
    }
}