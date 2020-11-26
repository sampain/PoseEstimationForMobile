package com.epmus.mobile.program

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.CollapsingToolbarLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.epmus.mobile.R
import com.epmus.mobile.globalExerciceList

/**
 * A fragment representing a single Program detail screen.
 * This fragment is either contained in a [ProgramListActivity]
 * in two-pane mode (on tablets) or a [ProgramDetailActivity]
 * on handsets.
 */
class ProgramDetailFragment : Fragment() {

    /**
     * The dummy content this fragment is presenting.
     */
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
            val id = resources.getIdentifier(it.imagePath, "drawable",
                activity?.packageName
            )
            rootView.findViewById<ImageView>(R.id.test).setImageResource(id)
        }

        return rootView
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}