package com.epmus.mobile.poseestimation

import java.io.Serializable

class MovementStatistics: Serializable {
    var angleAvg = ArrayList<Int?>()

    var position0_X = ArrayList<Float?>()
    var position0_Y = ArrayList<Float?>()

    var position1_X = ArrayList<Float?>()
    var position1_Y = ArrayList<Float?>()

    var position2_X = ArrayList<Float?>()
    var position2_Y = ArrayList<Float?>()
}