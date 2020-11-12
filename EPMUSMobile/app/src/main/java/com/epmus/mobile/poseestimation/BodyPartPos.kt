package com.epmus.mobile.poseestimation

import android.graphics.PointF
import java.io.Serializable

class BodyPartPos : Serializable {
    var HEAD: PointPos = PointPos()
    var NECK: PointPos = PointPos()
    var L_SHOULDER: PointPos = PointPos()
    var L_ELBOW: PointPos = PointPos()
    var L_WRIST: PointPos = PointPos()
    var R_SHOULDER:PointPos = PointPos()
    var R_ELBOW: PointPos = PointPos()
    var R_WRIST: PointPos = PointPos()
    var L_HIP: PointPos = PointPos()
    var L_KNEE: PointPos = PointPos()
    var L_ANKLE: PointPos = PointPos()
    var R_HIP: PointPos = PointPos()
    var R_KNEE: PointPos = PointPos()
    var R_ANKLE: PointPos = PointPos()
}