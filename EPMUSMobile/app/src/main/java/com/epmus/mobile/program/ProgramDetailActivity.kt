package com.epmus.mobile.program

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import com.epmus.mobile.R
import com.epmus.mobile.poseestimation.CameraActivity

/**
 * An activity representing a single Program detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [ProgramListActivity].
 */
class ProgramDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_program_detail)
        setSupportActionBar(findViewById(R.id.detail_toolbar))

        val exercise = intent.getParcelableExtra<ExerciseData>(ProgramDetailFragment.ARG_ITEM_ID)

        findViewById<FloatingActionButton>(R.id.fab_play).setOnClickListener { view ->
            val intent = Intent(view.context, CameraActivity::class.java)
            intent.putExtra("exercise", exercise)
            startActivity(intent)
        }

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don"t need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            val fragment = ProgramDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(
                        ProgramDetailFragment.ARG_ITEM_ID,
                        exercise?.id
                    )
                }
            }

            supportFragmentManager.beginTransaction()
                .add(R.id.program_detail_container, fragment)
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}