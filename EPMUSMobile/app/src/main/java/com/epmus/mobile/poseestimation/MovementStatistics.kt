package com.epmus.mobile.poseestimation

import java.io.Serializable

class MovementStatistics: Serializable {
    var angleAvg = ArrayList<Int?>()
    var state = ArrayList<MovementState?>()
}