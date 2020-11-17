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
import com.epmus.mobile.ui.login.LoginActivity
import com.epmus.mobile.ui.login.realmApp
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel

class StatisticsActivity : AppCompatActivity() {
    lateinit var tvR: TextView
    lateinit var tvPython: TextView
    lateinit var tvCPP: TextView
    lateinit var tvJava: TextView
    lateinit var pieChart: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_Statistics)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tvR = findViewById(R.id.tvR);
        tvPython = findViewById(R.id.tvPython);
        tvCPP = findViewById(R.id.tvCPP);
        tvJava = findViewById(R.id.tvJava);
        pieChart = findViewById(R.id.piechart);

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
        tvR.setText(40.toString())
        tvPython.setText(30.toString())
        tvCPP.setText(5.toString())
        tvJava.setText(25.toString())

        pieChart.addPieSlice(
            PieModel(
                "R", tvR.getText().toString().toInt().toFloat(),
                Color.parseColor("#FFA726")
            )
        )
        pieChart.addPieSlice(
            PieModel(
                "Python", tvPython.getText().toString().toInt().toFloat(),
                Color.parseColor("#66BB6A")
            )
        )
        pieChart.addPieSlice(
            PieModel(
                "C++", tvCPP.getText().toString().toInt().toFloat(),
                Color.parseColor("#EF5350")
            )
        )
        pieChart.addPieSlice(
            PieModel(
                "Java", tvJava.getText().toString().toInt().toFloat(),
                Color.parseColor("#29B6F6")
            )
        )

        pieChart.startAnimation()
    }
}