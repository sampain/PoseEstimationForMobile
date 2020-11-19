package com.epmus.mobile.poseestimation

import java.io.Serializable

class ExerciceStatistique : Serializable {
    var exerciceName = ""
    var exerciceType = ""

    var numberOfRepetition = ArrayList<Int?>()
    var speedOfRepetition = ArrayList<Float?>()
    var holdTime = ArrayList<Double?>()
    var timeStamp = ArrayList<String?>()

    var initStartTime: String? = null
    var exerciceStartTime: String? = null
    var exerciceEndTime: String? = null

    var movements = ArrayList<MovementStatistics>()

    var bodyPartPos : StatsBodyPartPos = StatsBodyPartPos()

    var patientID : String = ""
    var exerciceID : String = ""
}