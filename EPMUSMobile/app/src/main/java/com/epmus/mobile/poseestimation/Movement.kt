package com.epmus.mobile.poseestimation

import java.io.Serializable
import java.util.ArrayList

data class Movement(val bodyPart0_Index: Int, val bodyPart1_Index: Int, val bodyPart2_Index: Int) :
    Serializable {

    val acceptableAngleVariation: Int = 10
    var startingAngle: Int? = null
    var endingAngle: Int? = null
    var isAngleAntiClockWise: Boolean? = null

    var angleAvg: Int? = null
    var angleValuesLastFrames = ArrayList<Double>()

    var BodyPart0_X: Float? = null
    var BodyPart0_Y: Float? = null
    var BodyPart1_X: Float? = null
    var BodyPart1_Y: Float? = null
    var BodyPart2_X: Float? = null
    var BodyPart2_Y: Float? = null

    var member1Length: Int? = 0
    var member1LengthLastFrames = ArrayList<Double>()

    var member2Length: Int? = 0
    var member2LengthLastFrames = ArrayList<Double>()

    var angleOffset: Int? = null
    var angleOffsetLastFrames = ArrayList<Double>()

    var movementState: MovementState = MovementState.INIT
}