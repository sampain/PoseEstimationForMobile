package com.epmus.mobile.poseestimation

import java.io.Serializable

class ExerciceStatistique : Serializable {
    var exerciceName = ""
    var exerciceType = ""
    var exerciceID: String = ""

    var holdtime: String? = null
    var timestampOfRepetition = ArrayList<String?>()
    var numberOfRepetition = ArrayList<Int?>()

    var holdTimeStartTime = ArrayList<String?>()
    var holdTimeEndTime = ArrayList<String?>()

    var maxAngleAmplitude: Int? = null

    var initStartTime: String? = null
    var exerciceStartTime: String? = null
    var exerciceEndTime: String? = null

    var movements = ArrayList<MovementStatistics>()

    var avgFps: Double = -1.0
    var bodyPartPos: StatsBodyPartPos = StatsBodyPartPos()
}