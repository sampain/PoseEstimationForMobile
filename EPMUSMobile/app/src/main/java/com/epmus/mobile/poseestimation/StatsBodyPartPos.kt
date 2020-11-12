package com.epmus.mobile.poseestimation

import android.graphics.PointF
import java.io.Serializable

class StatsBodyPartPos : Serializable {
    var HEAD = ArrayList<PointPos>()
    var NECK = ArrayList<PointPos>()
    var L_SHOULDER = ArrayList<PointPos>()
    var L_ELBOW = ArrayList<PointPos>()
    var L_WRIST = ArrayList<PointPos>()
    var R_SHOULDER = ArrayList<PointPos>()
    var R_ELBOW = ArrayList<PointPos>()
    var R_WRIST = ArrayList<PointPos>()
    var L_HIP = ArrayList<PointPos>()
    var L_KNEE = ArrayList<PointPos>()
    var L_ANKLE = ArrayList<PointPos>()
    var R_HIP = ArrayList<PointPos>()
    var R_KNEE = ArrayList<PointPos>()
    var R_ANKLE = ArrayList<PointPos>()
}