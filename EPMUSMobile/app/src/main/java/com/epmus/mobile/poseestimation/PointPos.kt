package com.epmus.mobile.poseestimation

import java.io.Serializable

//Needed since PointF is not Serializable
class PointPos: Serializable {
    var X: Int = 0
    var Y: Int = 0

    constructor() {}
    constructor(x : Int, y : Int) {
        X = x
        Y = y
    }

}

