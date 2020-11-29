package com.epmus.mobile.program

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.CollapsingToolbarLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.epmus.mobile.R
import com.epmus.mobile.globalExerciceList
import com.epmus.mobile.poseestimation.ExerciceType

/**
 * A fragment representing a single Program detail screen.
 * This fragment is either contained in a [ProgramListActivity]
 * in two-pane mode (on tablets) or a [ProgramDetailActivity]
 * on handsets.
 */
class ProgramDetailFragment : Fragment() {
    private var item: ExerciceData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                globalExerciceList.forEach { exercice ->
                    if (exercice.id == it.getString(ARG_ITEM_ID)) {
                        item = exercice
                    }
                }

                activity?.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)?.title =
                    item?.name
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.program_detail, container, false)

        item?.let {
            rootView.findViewById<TextView>(R.id.program_detail).text = it.description
            it.exercice.movementList.forEachIndexed { index, movement ->
                if(index == 0){
                    rootView.findViewById<TextView>(R.id.angleStart).text = movement.startingAngle.toString()
                    rootView.findViewById<TextView>(R.id.angleEnd).text = movement.endingAngle.toString()
                }
                else{
                    rootView.findViewById<LinearLayout>(R.id.angle2Start_layout).visibility = View.VISIBLE
                    rootView.findViewById<LinearLayout>(R.id.angle2End_layout).visibility = View.VISIBLE
                    rootView.findViewById<TextView>(R.id.angle2Start).text = movement.startingAngle.toString()
                    rootView.findViewById<TextView>(R.id.angle2End).text = movement.endingAngle.toString()
                }

            }
            if (it.exercice.exerciceType == ExerciceType.HOLD) {
                rootView.findViewById<TextView>(R.id.repetitionOrHold_text).text = "Temps de maintient : "
            }
            rootView.findViewById<TextView>(R.id.repetitionOrHold).text = it.exercice.numberOfRepetitionToDo.toString()
            rootView.findViewById<TextView>(R.id.tempoMin).text = it.exercice.minExecutionTime.toString()
            rootView.findViewById<TextView>(R.id.tempoMax).text = it.exercice.maxExecutionTime.toString()
            val id = resources.getIdentifier(it.imagePath, "drawable",
                activity?.packageName
            )
            rootView.findViewById<ImageView>(R.id.test).setImageResource(id)
        }

        return rootView
    }

    companion object {
        const val ARG_ITEM_ID = "item_id"
    }
}