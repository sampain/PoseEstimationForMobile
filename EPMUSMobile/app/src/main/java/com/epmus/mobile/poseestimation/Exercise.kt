package com.epmus.mobile.poseestimation

import android.graphics.PointF
import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList
import kotlin.math.*

class Exercise() : Parcelable {
    // add to fun .copy() if there is a modif
    var maxExecutionTime: Float? = null
    var minExecutionTime: Float? = null
    private var mouvementStartTimer: Long? = null
    var mouvementSpeedTime: Float? = null

    var timeStamp: Long? = null

    var numberOfRepetitionToDo: Int? = null
    var numberOfRepetition: Int = 0
    var exitStateReached: Boolean = false

    var movementList = ArrayList<Movement>()

    var initStartTimer: Long? = null
    var initList = ArrayList<ArrayList<PointF>>()
    var notMovingInitList = ArrayList<Boolean>()
    var isInit: Boolean = false
    var exerciseStartTime: Long? = null
    private var notMovingStartTime: Long? = null
    var notMovingTimer: Int = -1
    var targetTime: Long = 4000
    private var stdMax: Int = 150
    var exerciseEndTime: Long? = null

    var exerciseType: ExerciseType? = null

    //Variable for type CHRONO
    var chronoTime: Int? = 0
    var allowedTimeForExercise: Int? = null

    //Variable for type HOLD
    var targetHoldTime: Int? = null
    var holdTime: Long = 0.toLong()
    private var wasHolding: Boolean = false
    var isHolding: Boolean = false
    private var holdingStartTime: Long? = null
    var currentHoldTime: Long = 0

    //Variable for type AMPLITUDE
    var maxAngleReached: Int? = null
    private var maxAngleReachedTime: Long? = null
    private var timeAllowedToReachNewMax: Int = 2

    //This is used to make sure that a warning cannot be spammed
    var warningCanBeDisplayed: Boolean = true

    // Track all bodypart (mostly used for statistics)
    var bp: BodyPartPos = BodyPartPos()

    constructor(parcel: Parcel) : this() {
        maxExecutionTime = parcel.readValue(Float::class.java.classLoader) as? Float
        minExecutionTime = parcel.readValue(Float::class.java.classLoader) as? Float
        mouvementStartTimer = parcel.readValue(Long::class.java.classLoader) as? Long
        mouvementSpeedTime = parcel.readValue(Float::class.java.classLoader) as? Float
        numberOfRepetitionToDo = parcel.readValue(Int::class.java.classLoader) as? Int
        numberOfRepetition = parcel.readInt()
        exitStateReached = parcel.readByte() != 0.toByte()
        movementList = parcel.readSerializable() as ArrayList<Movement>
        initStartTimer = parcel.readValue(Long::class.java.classLoader) as? Long
        initList = parcel.readSerializable() as ArrayList<ArrayList<PointF>>
        notMovingInitList = parcel.readSerializable() as ArrayList<Boolean>
        isInit = parcel.readByte() != 0.toByte()
        notMovingStartTime = parcel.readValue(Long::class.java.classLoader) as? Long
        notMovingTimer = parcel.readInt()
        targetTime = parcel.readLong()
        stdMax = parcel.readInt()
        exerciseType = parcel.readSerializable() as ExerciseType?
        chronoTime = parcel.readValue(Int::class.java.classLoader) as? Int
        allowedTimeForExercise = parcel.readValue(Int::class.java.classLoader) as? Int
        exerciseStartTime = parcel.readValue(Long::class.java.classLoader) as? Long
        targetHoldTime = parcel.readValue(Int::class.java.classLoader) as? Int
        holdTime = parcel.readLong()
        wasHolding = parcel.readByte() != 0.toByte()
        isHolding = parcel.readByte() != 0.toByte()
        holdingStartTime = parcel.readValue(Long::class.java.classLoader) as? Long
        currentHoldTime = parcel.readLong()
        warningCanBeDisplayed = parcel.readByte() != 0.toByte()
    }

    fun updateTimeStamp(dv: DrawView) {
        timeStamp = System.currentTimeMillis()

        bp.HEAD.X = round(dv.mDrawPoint[BodyPart.HEAD.ordinal].x).toInt()
        bp.HEAD.Y = round(dv.mDrawPoint[BodyPart.HEAD.ordinal].y).toInt()
        bp.NECK.X = round(dv.mDrawPoint[BodyPart.NECK.ordinal].x).toInt()
        bp.NECK.Y = round(dv.mDrawPoint[BodyPart.NECK.ordinal].y).toInt()
        bp.L_SHOULDER.X = round(dv.mDrawPoint[BodyPart.L_SHOULDER.ordinal].x).toInt()
        bp.L_SHOULDER.Y = round(dv.mDrawPoint[BodyPart.L_SHOULDER.ordinal].y).toInt()
        bp.L_ELBOW.X = round(dv.mDrawPoint[BodyPart.L_ELBOW.ordinal].x).toInt()
        bp.L_ELBOW.Y = round(dv.mDrawPoint[BodyPart.L_ELBOW.ordinal].y).toInt()
        bp.L_WRIST.X = round(dv.mDrawPoint[BodyPart.L_WRIST.ordinal].x).toInt()
        bp.L_WRIST.Y = round(dv.mDrawPoint[BodyPart.L_WRIST.ordinal].y).toInt()
        bp.R_SHOULDER.X = round(dv.mDrawPoint[BodyPart.R_SHOULDER.ordinal].x).toInt()
        bp.R_SHOULDER.Y = round(dv.mDrawPoint[BodyPart.R_SHOULDER.ordinal].y).toInt()
        bp.R_ELBOW.X = round(dv.mDrawPoint[BodyPart.R_ELBOW.ordinal].x).toInt()
        bp.R_ELBOW.Y = round(dv.mDrawPoint[BodyPart.R_ELBOW.ordinal].y).toInt()
        bp.R_WRIST.X = round(dv.mDrawPoint[BodyPart.R_WRIST.ordinal].x).toInt()
        bp.R_WRIST.Y = round(dv.mDrawPoint[BodyPart.R_WRIST.ordinal].y).toInt()
        bp.L_HIP.X = round(dv.mDrawPoint[BodyPart.L_HIP.ordinal].x).toInt()
        bp.L_HIP.Y = round(dv.mDrawPoint[BodyPart.L_HIP.ordinal].y).toInt()
        bp.L_KNEE.X = round(dv.mDrawPoint[BodyPart.L_KNEE.ordinal].x).toInt()
        bp.L_KNEE.Y = round(dv.mDrawPoint[BodyPart.L_KNEE.ordinal].y).toInt()
        bp.L_ANKLE.X = round(dv.mDrawPoint[BodyPart.L_ANKLE.ordinal].x).toInt()
        bp.L_ANKLE.Y = round(dv.mDrawPoint[BodyPart.L_ANKLE.ordinal].y).toInt()
        bp.R_HIP.X = round(dv.mDrawPoint[BodyPart.R_HIP.ordinal].x).toInt()
        bp.R_HIP.Y = round(dv.mDrawPoint[BodyPart.R_HIP.ordinal].y).toInt()
        bp.R_KNEE.X = round(dv.mDrawPoint[BodyPart.R_KNEE.ordinal].x).toInt()
        bp.R_KNEE.Y = round(dv.mDrawPoint[BodyPart.R_KNEE.ordinal].y).toInt()
        bp.R_ANKLE.X = round(dv.mDrawPoint[BodyPart.R_ANKLE.ordinal].x).toInt()
        bp.R_ANKLE.Y = round(dv.mDrawPoint[BodyPart.R_ANKLE.ordinal].y).toInt()
        bp.HIP.X = round(dv.mDrawPoint[BodyPart.HIP.ordinal].x).toInt()
        bp.HIP.Y = round(dv.mDrawPoint[BodyPart.HIP.ordinal].y).toInt()
    }

    fun initialisationVerification(drawView: DrawView) {
        //For Each body part
        initList.forEachIndexed()
        { index, item ->

            // Modify list
            val pointX: Float = drawView.mDrawPoint[index].x
            val pointY: Float = drawView.mDrawPoint[index].y
            val pF = PointF(pointX, pointY)
            if (!pointX.isNaN() && !pointY.isNaN()) {
                if (item.count() == drawView.frameCounterMaxInit) {
                    item.removeAt(0)
                }
                // add only if not 0 (out of frame)
                if (pointX.toInt() != 0 && pointY.toInt() != 0) {
                    item.add(pF)
                }
            }

            //Calculate average (mean) and standart deviation (ecart type)
            if (item.count() == drawView.frameCounterMaxInit) {
                //sum
                var totalX = 0.0000f
                var totalY = 0.0000f
                item.forEach()
                {
                    totalX += it.x
                    totalY += it.y
                }

                //mean
                val meanX = totalX / item.count()
                val meanY = totalY / item.count()

                //Variance
                var varianceX = 0.0000f
                var varianceY = 0.0000f
                item.forEach()
                {
                    val differenceX = it.x - meanX
                    varianceX += (differenceX * differenceX)
                    val differenceY = it.y - meanY
                    varianceY += (differenceY * differenceY)
                }

                //standart deviation
                val stdDevX = sqrt(varianceX)
                val stdDevY = sqrt(varianceX)

                //if std is below max, target is not moving
                notMovingInitList[index] = stdDevX <= stdMax && stdDevY <= stdMax
            }
        }

        //save the start of the initialization
        if (initStartTimer == null) {
            initStartTimer = System.currentTimeMillis()
        }

        //look if every body part are not moving
        var isNotMoving = true
        notMovingInitList.forEach()
        {
            if (!it) {
                isNotMoving = false
            }
        }

        if (isNotMoving) {
            if (notMovingStartTime == null) {
                notMovingStartTime = System.currentTimeMillis()
                notMovingTimer = -1
            } else {
                val currentTime: Long = System.currentTimeMillis()
                notMovingTimer =
                    targetTime.toInt() / 1000 - ((currentTime - notMovingStartTime!!) / 1000).toInt()
                if (currentTime - notMovingStartTime!! >= targetTime) {
                    isInit = true
                    exerciseStartTime = System.currentTimeMillis()
                }
            }
        } else {
            notMovingStartTime = null
            notMovingTimer = -1
        }
    }

    // Verify the state in which every movement is for the given exercise
    fun exerciseVerification(drawView: DrawView) {
        when (this.exerciseType) {
            ExerciseType.CHRONO -> exerciseVerificationChrono(drawView)
            ExerciseType.REPETITION -> exerciseVerificationRepetition(drawView)
            ExerciseType.HOLD -> exerciseVerificationHold(drawView)
            ExerciseType.AMPLITUDE -> exerciseVerificationAmplitude(drawView)
            else -> {
            }
        }
    }

    //Verify the state for an exercise type in Amplitude
    private fun exerciseVerificationAmplitude(drawView: DrawView) {
        movementList.forEach()
        {

            //Calculate new values for this frame
            calculateMembersLength(it, drawView)
            calculateAngleV2(it, drawView)

            //Sets new state for movement according to if the angle is matching or not
            if (isAngleMatching(it)) {
                when (it.movementState) {
                    MovementState.INIT -> {
                        it.movementState = MovementState.STARTING_ANGLE_REACHED
                        mouvementStartTimer = System.currentTimeMillis()
                        maxAngleReachedTime = System.currentTimeMillis()
                    }
                }
            }

            if (movementList[0].movementState == MovementState.STARTING_ANGLE_REACHED) {
                if (sign((it.endingAngle!! - it.startingAngle!!).toDouble()) == 1.0) {
                    if (maxAngleReached == null || movementList[0].angleAvg!! > maxAngleReached!!) {
                        maxAngleReached = movementList[0].angleAvg
                        maxAngleReachedTime = System.currentTimeMillis()
                    }
                }

                if (sign((it.endingAngle!! - it.startingAngle!!).toDouble()) == -1.0) {
                    if (maxAngleReached == null || movementList[0].angleAvg!! < maxAngleReached!!) {
                        maxAngleReached = movementList[0].angleAvg
                        maxAngleReachedTime = System.currentTimeMillis()
                    }
                }
            }

            if (maxAngleReachedTime != null) {
                if (timeAllowedToReachNewMax < (System.currentTimeMillis() - maxAngleReachedTime!!) / 1000) {
                    exitStateReached = true
                    exerciseEndTime = System.currentTimeMillis()
                }
            }
        }
    }

    //Verify the state for an exercise type in CHRONO
    private fun exerciseVerificationChrono(drawView: DrawView) {
        movementList.forEach()
        {

            //Calculate new values for this frame
            calculateMembersLength(it, drawView)
            calculateAngleV2(it, drawView)

            //Sets new state for movement according to if the angle is matching or not
            if (isAngleMatching(it)) {
                when (it.movementState) {
                    MovementState.INIT -> {
                        it.movementState = MovementState.STARTING_ANGLE_REACHED
                        mouvementStartTimer = System.currentTimeMillis()
                    }
                    MovementState.STARTING_ANGLE_REACHED -> {
                        it.movementState = MovementState.WAITING_FOR_OTHER_MOVEMENT_ENDING_ANGLE
                    }
                    MovementState.ENDING_ANGLE_REACHED -> {
                        it.movementState = MovementState.WAITING_FOR_OTHER_MOVEMENT_STARTING_ANGLE
                    }
                }
            } else {
                when (it.movementState) {
                    MovementState.WAITING_FOR_OTHER_MOVEMENT_ENDING_ANGLE -> {
                        it.movementState = MovementState.STARTING_ANGLE_REACHED
                    }
                    MovementState.WAITING_FOR_OTHER_MOVEMENT_STARTING_ANGLE -> {
                        it.movementState = MovementState.ENDING_ANGLE_REACHED
                    }
                }
            }
        }

        //Verifies if repetition is done and changes state of movements to ENDING_ANGLE_REACHED
        if (isRepetitionSimultaneousExerciseDone(movementList)) {
            movementList.forEach()
            {
                it.movementState = MovementState.ENDING_ANGLE_REACHED
            }
            numberOfRepetition++
            warningCanBeDisplayed = true
            mouvementSpeedTime = calculateTime()
        }

        //Verifies if each movement is at startingAngle is done and changes state of movements to ENDING_ANGLE_REACHED
        if (isInStartingPositionSimultaneousExercise(movementList)) {
            movementList.forEach()
            {
                it.movementState = MovementState.STARTING_ANGLE_REACHED
            }
        }

        //Calculates remaining chrono time
        chronoTime = ((System.currentTimeMillis() - exerciseStartTime!!) / 1000).toInt()
        chronoTime = allowedTimeForExercise!! - chronoTime!!

        //If no time is left, then the exercise is done
        if (chronoTime!! == 0) {
            exitStateReached = true
            exerciseEndTime = System.currentTimeMillis()
        }
    }

    private fun exerciseVerificationHold(drawView: DrawView) {
        movementList.forEach()
        {

            //Sets initial value of movement state to STARTING_ANGLE_REACHED since startingAngle is not used for this exercise type
            if (it.movementState == MovementState.INIT) {
                it.movementState = MovementState.STARTING_ANGLE_REACHED
            }

            //Calculate new values for this frame
            calculateMembersLength(it, drawView)
            calculateAngleV2(it, drawView)

            //Sets new state for movement according to if the angle is matching or not
            if (isAngleMatching(it)) {
                when (it.movementState) {
                    MovementState.STARTING_ANGLE_REACHED -> {
                        it.movementState = MovementState.WAITING_FOR_OTHER_MOVEMENT_ENDING_ANGLE
                        mouvementStartTimer = System.currentTimeMillis()
                    }
                }
            } else {
                when (it.movementState) {
                    MovementState.WAITING_FOR_OTHER_MOVEMENT_ENDING_ANGLE -> {
                        it.movementState = MovementState.STARTING_ANGLE_REACHED
                        holdTime += currentHoldTime
                        currentHoldTime = 0
                        wasHolding = false
                        warningCanBeDisplayed = true
                    }
                }
            }
        }

        //Verify if the patient was not holding the correct position and is now holding to set the holdingStartTime
        isInHoldPosition(movementList)
        if (isHolding && !wasHolding) {
            holdingStartTime = System.currentTimeMillis()
            wasHolding = true
            warningCanBeDisplayed = true
        }

        //Verify if the targetHoldTime is reached and set exit state to true
        if (holdingStartTime != null && isHolding) {
            currentHoldTime = System.currentTimeMillis() - holdingStartTime!!

            if (((holdTime + currentHoldTime) / 1000).toInt() >= targetHoldTime!!) {
                exitStateReached = true
                exerciseEndTime = System.currentTimeMillis()
                holdTime += currentHoldTime
                isHolding = false
                currentHoldTime = 0
            }
        }
    }

    //Verify if every movement is at state WAITING_FOR_OTHER_MOVEMENT_ENDING_ANGLE
    private fun isInHoldPosition(movementList: ArrayList<Movement>) {
        var isHolding = true
        movementList.forEach()
        {
            if (it.movementState != MovementState.WAITING_FOR_OTHER_MOVEMENT_ENDING_ANGLE) {
                isHolding = false
            }
        }
        this.isHolding = isHolding
    }

    private fun exerciseVerificationRepetition(drawView: DrawView) {
        movementList.forEach()
        {

            //Calculate new values for this frame
            calculateMembersLength(it, drawView)
            calculateAngleV2(it, drawView)

            //Sets new state for movement according to if the angle is matching or not
            if (isAngleMatching(it)) {
                when (it.movementState) {
                    MovementState.INIT -> {
                        it.movementState = MovementState.STARTING_ANGLE_REACHED
                        mouvementStartTimer = System.currentTimeMillis()
                    }
                    MovementState.STARTING_ANGLE_REACHED -> {
                        it.movementState = MovementState.WAITING_FOR_OTHER_MOVEMENT_ENDING_ANGLE
                    }
                    MovementState.ENDING_ANGLE_REACHED -> {
                        it.movementState = MovementState.WAITING_FOR_OTHER_MOVEMENT_STARTING_ANGLE
                    }
                }
            } else {
                when (it.movementState) {
                    MovementState.WAITING_FOR_OTHER_MOVEMENT_ENDING_ANGLE -> {
                        it.movementState = MovementState.STARTING_ANGLE_REACHED
                    }
                    MovementState.WAITING_FOR_OTHER_MOVEMENT_STARTING_ANGLE -> {
                        it.movementState = MovementState.ENDING_ANGLE_REACHED
                    }
                }
            }
        }

        //Verifies if repetition is done and changes state of movements to ENDING_ANGLE_REACHED
        if (isRepetitionSimultaneousExerciseDone(movementList)) {
            movementList.forEach()
            {
                it.movementState = MovementState.ENDING_ANGLE_REACHED
            }
            numberOfRepetition++
            warningCanBeDisplayed = true
            mouvementSpeedTime = calculateTime()
        }

        //Verifies if each movement is at startingAngle is done and changes state of movements to ENDING_ANGLE_REACHED
        if (isInStartingPositionSimultaneousExercise(movementList)) {
            movementList.forEach()
            {
                it.movementState = MovementState.STARTING_ANGLE_REACHED
            }
        }

        //Verify if the number of repetition is reached and sets exit value to true
        if (numberOfRepetitionToDo != null) {
            if (numberOfRepetitionToDo == numberOfRepetition) {
                exitStateReached = true
                exerciseEndTime = System.currentTimeMillis()
            }
        }
    }

    //Calculates the length of member1 and member2 for a given movement
    private fun calculateMembersLength(movement: Movement, drawView: DrawView) {
        val pointX0: Float = drawView.mDrawPoint[movement.bodyPart0_Index].x
        val pointY0: Float = drawView.mDrawPoint[movement.bodyPart0_Index].y
        val pointX1: Float = drawView.mDrawPoint[movement.bodyPart1_Index].x
        val pointY1: Float = drawView.mDrawPoint[movement.bodyPart1_Index].y
        val pointX2: Float = drawView.mDrawPoint[movement.bodyPart2_Index].x
        val pointY2: Float = drawView.mDrawPoint[movement.bodyPart2_Index].y

        val X1ToX0: Float = pointX0 - pointX1
        val Y1ToY0: Float = pointY0 - pointY1
        val X1ToX2: Float = pointX2 - pointX1
        val Y1ToY2: Float = pointY2 - pointY1

        val member1Length = sqrt(X1ToX0.pow(2) + Y1ToY0.pow(2))
        val member2Length = sqrt(X1ToX2.pow(2) + Y1ToY2.pow(2))

        if (!member1Length.isNaN() && !member2Length.isNaN()) {
            if (movement.member1LengthLastFrames.size == drawView.frameCounterMax && movement.member2LengthLastFrames.size == drawView.frameCounterMax) {
                movement.member1LengthLastFrames.removeAt(0)
                movement.member2LengthLastFrames.removeAt(0)
            }
            movement.member1LengthLastFrames.add(member1Length.toDouble())
            movement.member2LengthLastFrames.add(member2Length.toDouble())
        }

        if (movement.member1LengthLastFrames.size != 0 && movement.member2LengthLastFrames.size != 0) {
            movement.member1Length = movement.member1LengthLastFrames.average().roundToInt()
            movement.member2Length = movement.member2LengthLastFrames.average().roundToInt()
        }
    }

    //Verify if every movement for a given exercise is in state WAITING_FOR_OTHER_MOVEMENT_STARTING_ANGLE
    private fun isInStartingPositionSimultaneousExercise(movementList: ArrayList<Movement>): Boolean {
        var inStartingPosition = true
        movementList.forEach()
        {
            if (it.movementState != MovementState.WAITING_FOR_OTHER_MOVEMENT_STARTING_ANGLE) {
                inStartingPosition = false
            }
        }
        return inStartingPosition
    }

    fun calculateAngleHorizontalOffset(
        movement: Movement,
        drawView: DrawView,
        bodyPartCenterOfRotation: Int,
        endBodyPart: Int
    ) {
        val bodyPartCenterOfRotationX = drawView.mDrawPoint[bodyPartCenterOfRotation].x
        val bodyPartCenterOfRotationY = drawView.mDrawPoint[bodyPartCenterOfRotation].y

        val endBodyPartX = drawView.mDrawPoint[endBodyPart].x
        val endBodyPartY = drawView.mDrawPoint[endBodyPart].y

        val deltaY = endBodyPartY - bodyPartCenterOfRotationY
        val deltaX = endBodyPartX - bodyPartCenterOfRotationX

        val angleRad: Float = atan(deltaY / deltaX)
        var angleDeg: Double = ((angleRad * 180) / Math.PI)

        //First quadrant
        if (sign(deltaX).toInt() == 1 && sign(deltaY).toInt() == 1) {
            // nothing
        }

        //Second quadrant
        else if (sign(deltaX).toInt() == -1 && sign(deltaY).toInt() == 1) {
            angleDeg += 180
        }

        //Third quadrant
        else if (sign(deltaX).toInt() == -1 && sign(deltaY).toInt() == -1) {
            angleDeg = -1 * (180 - angleDeg)
        }

        //Fourth quadrant
        else if (sign(deltaX).toInt() == 1 && sign(deltaY).toInt() == -1) {
            // nothing
        } else if (sign(deltaX).toInt() == -1 && sign(deltaY).toInt() == 0) {
            angleDeg = 180.0
        }


        if (!angleDeg.isNaN()) {
            if (movement.angleOffsetLastFrames.size == drawView.frameCounterMax) {
                movement.angleOffsetLastFrames.removeAt(0)
            }
            movement.angleOffsetLastFrames.add(angleDeg)
        }

        if (movement.angleOffsetLastFrames.size != 0)
            movement.angleOffset = movement.angleOffsetLastFrames.average().roundToInt()

    }

    //Verify if every movement for a given exercise is in state WAITING_FOR_OTHER_MOVEMENT_ENDING_ANGLE
    private fun isRepetitionSimultaneousExerciseDone(movementList: ArrayList<Movement>): Boolean {
        var repetitionDone = true
        movementList.forEach()
        {
            if (it.movementState != MovementState.WAITING_FOR_OTHER_MOVEMENT_ENDING_ANGLE) {
                repetitionDone = false
            }
        }
        return repetitionDone
    }

    private fun correctAngle(
        needToCorrectClockWise: Boolean,
        isClockWise: Boolean,
        angles: Double
    ): Double {
        if (needToCorrectClockWise) {
            if (isClockWise) {
                return 360 - angles
            } else {
                return angles
            }
        } else {
            if (isClockWise) {
                return angles
            } else {
                return 360 - angles
            }
        }
    }

    //Calculates the angle between the three points in a movement
    private fun calculateAngleV2(movement: Movement, drawView: DrawView) {
        //*-1 because Y is inverted
        val pointX0: Float = drawView.mDrawPoint[movement.bodyPart0_Index].x
        val pointY0: Float = drawView.mDrawPoint[movement.bodyPart0_Index].y
        val pointX1: Float = drawView.mDrawPoint[movement.bodyPart1_Index].x
        val pointY1: Float = drawView.mDrawPoint[movement.bodyPart1_Index].y
        val pointX2: Float = drawView.mDrawPoint[movement.bodyPart2_Index].x
        val pointY2: Float = drawView.mDrawPoint[movement.bodyPart2_Index].y

        val X1ToX0: Float = pointX0 - pointX1
        val Y1ToY0: Float = pointY0 - pointY1
        val X1ToX2: Float = pointX2 - pointX1
        val Y1ToY2: Float = pointY2 - pointY1

        val X1X0mod: Float = sqrt((X1ToX0 * X1ToX0) + (Y1ToY0 * Y1ToY0))
        val X1X2mod: Float = sqrt((X1ToX2 * X1ToX2) + (Y1ToY2 * Y1ToY2))

        val vectorProduct: Float = X1ToX0 * X1ToX2 + Y1ToY0 * Y1ToY2

        val angleRad: Float = acos(vectorProduct / (X1X0mod * X1X2mod))
        var angleDeg: Double = ((angleRad * 180) / Math.PI)

        //Adding anti/clockwise effect
        //vertical PointO-Point1
        if (X1ToX0 == 0F) {
            if (pointY1 < pointY0) {
                if (pointX2 < pointX1) {
                    angleDeg = correctAngle(false, movement.isAngleClockWise!!, angleDeg)
                } else {
                    angleDeg = correctAngle(true, movement.isAngleClockWise!!, angleDeg)
                }
            } else {
                if (pointX2 < pointX1) {
                    angleDeg = correctAngle(true, movement.isAngleClockWise!!, angleDeg)
                } else {
                    angleDeg = correctAngle(false, movement.isAngleClockWise!!, angleDeg)
                }
            }
        }
        //horizontal Point0-Point1
        else if (Y1ToY0 == 0F) {
            if (pointX1 > pointX0) {
                if (pointY2 > pointY1) {
                    angleDeg = correctAngle(true, movement.isAngleClockWise!!, angleDeg)
                } else {
                    angleDeg = correctAngle(false, movement.isAngleClockWise!!, angleDeg)
                }
            } else {
                if (pointY2 > pointY1) {
                    angleDeg = correctAngle(false, movement.isAngleClockWise!!, angleDeg)
                } else {
                    angleDeg = correctAngle(true, movement.isAngleClockWise!!, angleDeg)
                }
            }

        } else {
            val a = Y1ToY0 / X1ToX0
            val b = pointY0 - (a * pointX0)
            val tmpPointY2 = (a * pointX2) + b

            val dX = pointX1 - pointX0
            val dY = pointY1 - pointY0

            //Quadrant 1
            if (dX > 0 && dY < 0) {
                if (tmpPointY2 > pointY2) {
                    angleDeg = correctAngle(false, movement.isAngleClockWise!!, angleDeg)
                } else {
                    angleDeg = correctAngle(true, movement.isAngleClockWise!!, angleDeg)
                }
            }
            //Quadrant 2
            else if (dX < 0 && dY < 0) {
                if (tmpPointY2 > pointY2) {
                    angleDeg = correctAngle(true, movement.isAngleClockWise!!, angleDeg)
                } else {
                    angleDeg = correctAngle(false, movement.isAngleClockWise!!, angleDeg)
                }

            }
            //Quadrant 3
            else if (dX < 0 && dY > 0) {
                if (tmpPointY2 > pointY2) {
                    angleDeg = correctAngle(true, movement.isAngleClockWise!!, angleDeg)
                } else {
                    angleDeg = correctAngle(false, movement.isAngleClockWise!!, angleDeg)
                }

            }
            //Quadrant 4
            else if (dX > 0 && dY > 0) {
                if (tmpPointY2 > pointY2) {
                    angleDeg = correctAngle(false, movement.isAngleClockWise!!, angleDeg)
                } else {
                    angleDeg = correctAngle(true, movement.isAngleClockWise!!, angleDeg)
                }

            }
        }


        if (!angleDeg.isNaN()) {
            if (movement.angleValuesLastFrames.size == drawView.frameCounterMax) {
                movement.angleValuesLastFrames.removeAt(0)
            }
            movement.angleValuesLastFrames.add(angleDeg)
        }

        if (movement.angleValuesLastFrames.size != 0)
            movement.angleAvg = movement.angleValuesLastFrames.average().roundToInt()

    }


    //Verify if the angle is matching according to the state of the movement
    private fun isAngleMatching(movement: Movement): Boolean {
        if (movement.angleAvg != null) {
            when (movement.movementState) {
                MovementState.INIT, MovementState.ENDING_ANGLE_REACHED, MovementState.WAITING_FOR_OTHER_MOVEMENT_STARTING_ANGLE -> {
                    return movement.angleAvg!! > movement.startingAngle!! - movement.acceptableAngleVariation && movement.angleAvg!! < movement.startingAngle!! + movement.acceptableAngleVariation
                }
                MovementState.STARTING_ANGLE_REACHED, MovementState.WAITING_FOR_OTHER_MOVEMENT_ENDING_ANGLE -> {
                    return movement.angleAvg!! > movement.endingAngle!! - movement.acceptableAngleVariation && movement.angleAvg!! < movement.endingAngle!! + movement.acceptableAngleVariation
                }
                else -> {
                    return false
                }
            }
        } else {
            return false
        }
    }

    private fun calculateTime(): Float? {
        var timeInSecond: Float? = null
        if (mouvementStartTimer != null) {
            timeInSecond = ((System.currentTimeMillis() - mouvementStartTimer!!).toFloat()) / 1000
        }
        mouvementStartTimer = System.currentTimeMillis()
        return timeInSecond
    }


    fun copy(): Exercise {
        val exercise = Exercise()
        exercise.maxExecutionTime = maxExecutionTime
        exercise.minExecutionTime = minExecutionTime
        exercise.mouvementStartTimer = mouvementStartTimer
        exercise.mouvementSpeedTime = mouvementSpeedTime

        exercise.timeStamp = timeStamp

        exercise.numberOfRepetitionToDo = numberOfRepetitionToDo
        exercise.numberOfRepetition = numberOfRepetition
        exercise.exitStateReached = exitStateReached

        movementList.forEach {
            val tmpMovement = Movement(it.bodyPart0_Index, it.bodyPart1_Index, it.bodyPart2_Index)
            tmpMovement.startingAngle = it.startingAngle
            tmpMovement.endingAngle = it.endingAngle
            tmpMovement.isAngleClockWise = it.isAngleClockWise
            tmpMovement.angleAvg = it.angleAvg
            tmpMovement.member1Length = it.member1Length
            tmpMovement.member2Length = it.member2Length
            tmpMovement.angleOffset = it.angleOffset
            tmpMovement.movementState = it.movementState
            tmpMovement.member1LengthLastFrames = it.member1LengthLastFrames
            tmpMovement.member2LengthLastFrames = it.member2LengthLastFrames
            tmpMovement.angleOffsetLastFrames = it.angleOffsetLastFrames
            tmpMovement.angleValuesLastFrames = it.angleValuesLastFrames

            exercise.movementList.add(tmpMovement)
        }

        exercise.initStartTimer = initStartTimer
        exercise.initList = initList
        exercise.notMovingInitList = notMovingInitList
        exercise.isInit = isInit
        exercise.exerciseStartTime = exerciseStartTime
        exercise.notMovingStartTime = notMovingStartTime
        exercise.notMovingTimer = notMovingTimer
        exercise.targetTime = targetTime
        exercise.stdMax = stdMax
        exercise.exerciseEndTime = exerciseEndTime

        exercise.exerciseType = exerciseType

        exercise.chronoTime = chronoTime
        exercise.allowedTimeForExercise = allowedTimeForExercise

        exercise.targetHoldTime = targetHoldTime
        exercise.holdTime = holdTime
        exercise.wasHolding = wasHolding
        exercise.isHolding = isHolding
        exercise.holdingStartTime = holdingStartTime
        exercise.currentHoldTime = currentHoldTime

        exercise.maxAngleReached = maxAngleReached
        exercise.maxAngleReachedTime = maxAngleReachedTime
        exercise.timeAllowedToReachNewMax = timeAllowedToReachNewMax

        exercise.warningCanBeDisplayed = warningCanBeDisplayed

        exercise.bp.HEAD.X = bp.HEAD.X
        exercise.bp.HEAD.Y = bp.HEAD.Y
        exercise.bp.NECK.X = bp.NECK.X
        exercise.bp.NECK.Y = bp.NECK.Y
        exercise.bp.L_SHOULDER.X = bp.L_SHOULDER.X
        exercise.bp.L_SHOULDER.Y = bp.L_SHOULDER.Y
        exercise.bp.L_ELBOW.X = bp.L_ELBOW.X
        exercise.bp.L_ELBOW.Y = bp.L_ELBOW.Y
        exercise.bp.L_WRIST.X = bp.L_WRIST.X
        exercise.bp.L_WRIST.Y = bp.L_WRIST.Y
        exercise.bp.R_SHOULDER.X = bp.R_SHOULDER.X
        exercise.bp.R_SHOULDER.Y = bp.R_SHOULDER.Y
        exercise.bp.R_ELBOW.X = bp.R_ELBOW.X
        exercise.bp.R_ELBOW.Y = bp.R_ELBOW.Y
        exercise.bp.R_WRIST.X = bp.R_WRIST.X
        exercise.bp.R_WRIST.Y = bp.R_WRIST.Y
        exercise.bp.L_HIP.X = bp.L_HIP.X
        exercise.bp.L_HIP.Y = bp.L_HIP.Y
        exercise.bp.L_KNEE.X = bp.L_KNEE.X
        exercise.bp.L_KNEE.Y = bp.L_KNEE.Y
        exercise.bp.L_ANKLE.X = bp.L_ANKLE.X
        exercise.bp.L_ANKLE.Y = bp.L_ANKLE.Y
        exercise.bp.R_HIP.X = bp.R_HIP.X
        exercise.bp.R_HIP.Y = bp.R_HIP.Y
        exercise.bp.R_KNEE.X = bp.R_KNEE.X
        exercise.bp.R_KNEE.Y = bp.R_KNEE.Y
        exercise.bp.R_ANKLE.X = bp.R_ANKLE.X
        exercise.bp.R_ANKLE.Y = bp.R_ANKLE.Y
        exercise.bp.HIP.X = bp.HIP.X
        exercise.bp.HIP.Y = bp.HIP.Y

        return exercise
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(maxExecutionTime)
        parcel.writeValue(minExecutionTime)
        parcel.writeValue(mouvementStartTimer)
        parcel.writeValue(mouvementSpeedTime)
        parcel.writeValue(numberOfRepetitionToDo)
        parcel.writeInt(numberOfRepetition)
        parcel.writeByte(if (exitStateReached) 1 else 0)
        parcel.writeSerializable(movementList)
        parcel.writeValue(initStartTimer)
        parcel.writeSerializable(initList)
        parcel.writeSerializable(notMovingInitList)
        parcel.writeByte(if (isInit) 1 else 0)
        parcel.writeValue(notMovingStartTime)
        parcel.writeInt(notMovingTimer)
        parcel.writeLong(targetTime)
        parcel.writeInt(stdMax)
        parcel.writeSerializable(exerciseType)
        parcel.writeValue(chronoTime)
        parcel.writeValue(allowedTimeForExercise)
        parcel.writeValue(exerciseStartTime)
        parcel.writeValue(targetHoldTime)
        parcel.writeLong(holdTime)
        parcel.writeByte(if (wasHolding) 1 else 0)
        parcel.writeByte(if (isHolding) 1 else 0)
        parcel.writeValue(holdingStartTime)
        parcel.writeLong(currentHoldTime)
        parcel.writeByte(if (warningCanBeDisplayed) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Exercise> {
        override fun createFromParcel(parcel: Parcel): Exercise {
            return Exercise(parcel)
        }

        override fun newArray(size: Int): Array<Exercise?> {
            return arrayOfNulls(size)
        }
    }
}


