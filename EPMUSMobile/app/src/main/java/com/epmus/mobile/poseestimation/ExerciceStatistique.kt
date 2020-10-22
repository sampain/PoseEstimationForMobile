package com.epmus.mobile.poseestimation

import java.io.Serializable

class ExerciceStatistique : Serializable {
    var timeStamp = ArrayList<Long?>()
    var numberOfRepetition = ArrayList<Int?>()
    var speedOfRepetition = ArrayList<Float?>()
    var holdTime = ArrayList<Long?>()
    var notMovingInitList = ArrayList<ArrayList<Boolean>>()

    var initStartTime: Long? = null
    var exerciceStartTime: Long? = null
    var exerciceEndTime: Long? = null

    var movements = ArrayList<MovementStatistics>()
}