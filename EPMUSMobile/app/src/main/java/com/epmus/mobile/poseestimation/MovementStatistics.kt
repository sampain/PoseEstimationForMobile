package com.epmus.mobile.poseestimation

import java.io.Serializable

class MovementStatistics: Serializable {
    var timestampState = ArrayList<String?>()
    var state = ArrayList<MovementState?>()
}