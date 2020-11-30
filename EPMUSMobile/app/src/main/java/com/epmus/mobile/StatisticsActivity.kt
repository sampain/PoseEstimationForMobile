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
import com.epmus.mobile.ui.login.realmApp
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess

class StatisticsActivity : AppCompatActivity() {
    private lateinit var statisticCount: TextView
    private lateinit var statisticCount7: TextView
    private lateinit var pieChart: PieChart
    private lateinit var history: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_Statistics)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        statisticCount = findViewById(R.id.statistic_count)
        statisticCount7 = findViewById(R.id.statistic_count_7)
        pieChart = findViewById(R.id.piechart)
        history = findViewById(R.id.history_number)

        val history = findViewById<CardView>(R.id.history_button)

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

    private fun setData() {
        statisticCount.text = historic.count().toString()

        var holdCount = 0
        var repetitionCount = 0
        var chronoCount = 0
        var ampCount = 0
        var count7 = 0

        val localDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        historic.forEach {

            when (ExerciceType.getEnumValue(it.exerciceType)) {
                ExerciceType.HOLD -> {
                    holdCount++
                }
                ExerciceType.REPETITION -> {
                    repetitionCount++
                }
                ExerciceType.AMPLITUDE -> {
                    ampCount++
                }
                ExerciceType.CHRONO -> {
                    chronoCount++
                }
            }

            if (LocalDate.parse(it.date, formatter).compareTo(localDate) >= -7) {
                count7++
            }
        }

        statisticCount7.text = count7.toString()

        history.text = historic.count().toString()

        pieChart.addPieSlice(
            PieModel(
                "Type Maintenir", holdCount.toFloat(),
                Color.parseColor("#66BB6A")
            )
        )
        pieChart.addPieSlice(
            PieModel(
                "Type Répétition", repetitionCount.toFloat(),
                Color.parseColor("#FF9800")
            )
        )
        pieChart.addPieSlice(
            PieModel(
                "Type Chronomètre", chronoCount.toFloat(),
                Color.parseColor("#29B6F6")
            )
        )
        pieChart.addPieSlice(
            PieModel(
                "Type Amplitude", ampCount.toFloat(),
                Color.parseColor("#774C55")
            )
        )

        pieChart.startAnimation()
    }
}