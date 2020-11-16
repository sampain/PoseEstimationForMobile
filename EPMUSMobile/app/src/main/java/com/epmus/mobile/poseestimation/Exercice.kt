package com.epmus.mobile.poseestimation

import android.graphics.Point
import android.graphics.PointF
import android.os.Parcel
import android.os.Parcelable
import android.widget.Chronometer
import java.util.ArrayList
import kotlin.math.*

class Exercice() : Parcelable {
    // add to fun .copy() if there is a modif
    var maxExecutionTime: Float? = null
    var minExecutionTime: Float? = null
    var mouvementStartTimer: Long? = null
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
    var exerciceStartTime: Long? = null
    var notMovingStartTime: Long? = null
    var notMovingTimer: Int = 0
    var targetTime: Long = 4000
    var stdMax: Int = 100
    var exerciceEndTime: Long? = null

    var exerciceType: ExerciceType? = null

    //Variable for type CHRONO
    var chronoTime: Int? = 0
    var allowedTimeForExercice: Int? = null

    //Variable for type HOLD
    var targetHoldTime: Int? = null
    var holdTime: Long = 0.toLong()
    var wasHolding: Boolean = false
    var isHolding: Boolean = false
    var holdingStartTime: Long? = null
    var currentHoldTime: Long = 0

    //This is used to make sure that a warning cannot be spammed
    var warningCanBeDisplayed: Boolean = true

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
        exerciceType = parcel.readSerializable() as ExerciceType?
        chronoTime = parcel.readValue(Int::class.java.classLoader) as? Int
        allowedTimeForExercice = parcel.readValue(Int::class.java.classLoader) as? Int
        exerciceStartTime = parcel.readValue(Long::class.java.classLoader) as? Long
        targetHoldTime = parcel.readValue(Int::class.java.classLoader) as? Int
        holdTime = parcel.readLong()
        wasHolding = parcel.readByte() != 0.toByte()
        isHolding = parcel.readByte() != 0.toByte()
        holdingStartTime = parcel.readValue(Long::class.java.classLoader) as? Long
        currentHoldTime = parcel.readLong()
        warningCanBeDisplayed = parcel.readByte() != 0.toByte()
    }

    // Track all bodypart (mostly used for statistics)
    var bp : BodyPartPos = BodyPartPos()

    fun updateTimeStamp (dv : DrawView) {
        timeStamp = System.currentTimeMillis()

        bp.HEAD.X = round(dv!!.mDrawPoint[BodyPart.HEAD.ordinal].x).toInt()
        bp.HEAD.Y = round(dv!!.mDrawPoint[BodyPart.HEAD.ordinal].y).toInt()
        bp.NECK.X = round(dv!!.mDrawPoint[BodyPart.NECK.ordinal].x).toInt()
        bp.NECK.Y = round(dv!!.mDrawPoint[BodyPart.NECK.ordinal].y).toInt()
        bp.L_SHOULDER.X = round(dv!!.mDrawPoint[BodyPart.L_SHOULDER.ordinal].x).toInt()
        bp.L_SHOULDER.Y = round(dv!!.mDrawPoint[BodyPart.L_SHOULDER.ordinal].y).toInt()
        bp.L_ELBOW.X = round(dv!!.mDrawPoint[BodyPart.L_ELBOW.ordinal].x).toInt()
        bp.L_ELBOW.Y = round(dv!!.mDrawPoint[BodyPart.L_ELBOW.ordinal].y).toInt()
        bp.L_WRIST.X = round(dv!!.mDrawPoint[BodyPart.L_WRIST.ordinal].x).toInt()
        bp.L_WRIST.Y = round(dv!!.mDrawPoint[BodyPart.L_WRIST.ordinal].y).toInt()
        bp.R_SHOULDER.X = round(dv!!.mDrawPoint[BodyPart.R_SHOULDER.ordinal].x).toInt()
        bp.R_SHOULDER.Y = round(dv!!.mDrawPoint[BodyPart.R_SHOULDER.ordinal].y).toInt()
        bp.R_ELBOW.X = round(dv!!.mDrawPoint[BodyPart.R_ELBOW.ordinal].x).toInt()
        bp.R_ELBOW.Y = round(dv!!.mDrawPoint[BodyPart.R_ELBOW.ordinal].y).toInt()
        bp.R_WRIST.X = round(dv!!.mDrawPoint[BodyPart.R_WRIST.ordinal].x).toInt()
        bp.R_WRIST.Y = round(dv!!.mDrawPoint[BodyPart.R_WRIST.ordinal].y).toInt()
        bp.L_HIP.X = round(dv!!.mDrawPoint[BodyPart.L_HIP.ordinal].x).toInt()
        bp.L_HIP.Y = round(dv!!.mDrawPoint[BodyPart.L_HIP.ordinal].y).toInt()
        bp.L_KNEE.X = round(dv!!.mDrawPoint[BodyPart.L_KNEE.ordinal].x).toInt()
        bp.L_KNEE.Y = round(dv!!.mDrawPoint[BodyPart.L_KNEE.ordinal].y).toInt()
        bp.L_ANKLE.X = round(dv!!.mDrawPoint[BodyPart.L_ANKLE.ordinal].x).toInt()
        bp.L_ANKLE.Y = round(dv!!.mDrawPoint[BodyPart.L_ANKLE.ordinal].y).toInt()
        bp.R_HIP.X = round(dv!!.mDrawPoint[BodyPart.R_HIP.ordinal].x).toInt()
        bp.R_HIP.Y = round(dv!!.mDrawPoint[BodyPart.R_HIP.ordinal].y).toInt()
        bp.R_KNEE.X = round(dv!!.mDrawPoint[BodyPart.R_KNEE.ordinal].x).toInt()
        bp.R_KNEE.Y = round(dv!!.mDrawPoint[BodyPart.R_KNEE.ordinal].y).toInt()
        bp.R_ANKLE.X = round(dv!!.mDrawPoint[BodyPart.R_ANKLE.ordinal].x).toInt()
        bp.R_ANKLE.Y = round(dv!!.mDrawPoint[BodyPart.R_ANKLE.ordinal].y).toInt()
    }

    fun initialisationVerification(drawView: DrawView) {
        //For Each body part
        initList.forEachIndexed()
        { index, item ->



            // Modify list
            var pointX: Float = drawView.mDrawPoint[index].x
            var pointY: Float = drawView.mDrawPoint[index].y
            var pF = PointF(pointX, pointY)
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
            var meanX: Float = -1.0f
            var meanY: Float = -1.0f
            var stdDevX: Float = -1.0f
            var stdDevY: Float = -1.0f
            if (item.count() == drawView.frameCounterMaxInit) {
                //sum
                var totalX: Float = 0.0000f
                var totalY: Float = 0.0000f
                item.forEach()
                {
                    totalX += it.x
                    totalY += it.y
                }

                //mean
                meanX = totalX / item.count()
                meanY = totalY / item.count()

                //Variance
                var varianceX: Float = 0.0000f
                var varianceY: Float = 0.0000f
                item.forEach()
                {
                    var differenceX = it.x - meanX
                    varianceX += (differenceX * differenceX)
                    var differenceY = it.y - meanY
                    varianceY += (differenceY * differenceY)
                }

                //standart deviation
                stdDevX = sqrt(varianceX)
                stdDevY = sqrt(varianceX)

                //if std is below max, target is not moving
                notMovingInitList[index] = stdDevX <= stdMax && stdDevY <= stdMax
            }
        }

        //save the start of the initialization
        if (initStartTimer == null) {
            initStartTimer = System.currentTimeMillis()
        }

        //look if every body part are not moving
        var isNotMoving: Boolean = true
        notMovingInitList.forEach()
        {
            if (it == false) {
                isNotMoving = false
            }
        }

        if (isNotMoving) {
            if (notMovingStartTime == null) {
                notMovingStartTime = System.currentTimeMillis()
                notMovingTimer = 5
            } else {
                var currentTime: Long = System.currentTimeMillis()
                notMovingTimer =
                    targetTime.toInt() / 1000 - ((currentTime - notMovingStartTime!!) / 1000).toInt()
                if (currentTime - notMovingStartTime!! >= targetTime) {
                    isInit = true
                    exerciceStartTime = System.currentTimeMillis()
                }
            }
        } else {
            notMovingStartTime = null
        }
    }

    // Verify the state in which every movement is for the given exercice
    fun exerciceVerification(drawView: DrawView) {
        when (this.exerciceType) {
            ExerciceType.CHRONO -> exerciceVerificationChrono(drawView)
            ExerciceType.REPETITION -> exerciceVerificationRepetition(drawView)
            ExerciceType.HOLD -> exerciceVerificationHold(drawView)
            else -> {}
        }
    }

    //Verify the state for an exercice type in CHRONO
    fun exerciceVerificationChrono(drawView: DrawView) {
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
        if (isRepetitionSimultaneousExerciceDone(movementList)) {
            movementList.forEach()
            {
                it.movementState = MovementState.ENDING_ANGLE_REACHED
            }
            numberOfRepetition++
            warningCanBeDisplayed = true
            mouvementSpeedTime = calculateTime()
        }

        //Verifies if each movement is at startingAngle is done and changes state of movements to ENDING_ANGLE_REACHED
        if (isInStartingPositionSimultaneousExercice(movementList)) {
            movementList.forEach()
            {
                it.movementState = MovementState.STARTING_ANGLE_REACHED
            }
        }

        //Calculates remaining chrono time
        chronoTime = ((System.currentTimeMillis() - exerciceStartTime!!)/1000).toInt()
        chronoTime = allowedTimeForExercice!! - chronoTime!!

        //If no time is left, then the exercice is done
        if (chronoTime!! == 0) {
            exitStateReached = true
            exerciceEndTime = System.currentTimeMillis()
        }
    }

    fun exerciceVerificationHold(drawView: DrawView) {
        movementList.forEach()
        {

            //Sets initial value of movement state to STARTING_ANGLE_REACHED since startingAngle is not used for this exercice type
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
                exerciceEndTime = System.currentTimeMillis()
                holdTime += currentHoldTime
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

    fun exerciceVerificationRepetition(drawView: DrawView) {
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
        if (isRepetitionSimultaneousExerciceDone(movementList)) {
            movementList.forEach()
            {
                it.movementState = MovementState.ENDING_ANGLE_REACHED
            }
            numberOfRepetition++
            warningCanBeDisplayed = true
            mouvementSpeedTime = calculateTime()
        }

        //Verifies if each movement is at startingAngle is done and changes state of movements to ENDING_ANGLE_REACHED
        if (isInStartingPositionSimultaneousExercice(movementList)) {
            movementList.forEach()
            {
                it.movementState = MovementState.STARTING_ANGLE_REACHED
            }
        }

        //Verify if the number of repetition is reached and sets exit value to true
        if (numberOfRepetitionToDo != null) {
            if (numberOfRepetitionToDo == numberOfRepetition) {
                exitStateReached = true
                exerciceEndTime = System.currentTimeMillis()
            }
        }
    }

    //Calculates the length of member1 and member2 for a given movement
    fun calculateMembersLength(movement: Movement, drawView: DrawView) {
        var pointX0: Float = drawView.mDrawPoint[movement.bodyPart0_Index].x
        var pointY0: Float = drawView.mDrawPoint[movement.bodyPart0_Index].y
        var pointX1: Float = drawView.mDrawPoint[movement.bodyPart1_Index].x
        var pointY1: Float = drawView.mDrawPoint[movement.bodyPart1_Index].y
        var pointX2: Float = drawView.mDrawPoint[movement.bodyPart2_Index].x
        var pointY2: Float = drawView.mDrawPoint[movement.bodyPart2_Index].y

        var X1ToX0: Float = pointX0 - pointX1
        var Y1ToY0: Float = pointY0 - pointY1
        var X1ToX2: Float = pointX2 - pointX1
        var Y1ToY2: Float = pointY2 - pointY1

        var member1Length = sqrt(X1ToX0.pow(2) + Y1ToY0.pow(2))
        var member2Length = sqrt(X1ToX2.pow(2) + Y1ToY2.pow(2))

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

    //Verify if every movement for a given exercice is in state WAITING_FOR_OTHER_MOVEMENT_STARTING_ANGLE
    private fun isInStartingPositionSimultaneousExercice(movementList: ArrayList<Movement>): Boolean {
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
        var bodyPartCenterOfRotationX = drawView.mDrawPoint[bodyPartCenterOfRotation].x
        var bodyPartCenterOfRotationY = drawView.mDrawPoint[bodyPartCenterOfRotation].y

        var endBodyPartX = drawView.mDrawPoint[endBodyPart].x
        var endBodyPartY = drawView.mDrawPoint[endBodyPart].y

        var deltaY = endBodyPartY - bodyPartCenterOfRotationY
        var deltaX = endBodyPartX - bodyPartCenterOfRotationX

        var angleRad: Float = atan(deltaY / deltaX)
        var angleDeg: Double = ((angleRad * 180) / Math.PI)

        //First quadrant
        if (sign(deltaX).toInt() == 1 && sign(deltaY).toInt() == 1) {

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
        else {

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

    //Verify if every movement for a given exercice is in state WAITING_FOR_OTHER_MOVEMENT_ENDING_ANGLE
    private fun isRepetitionSimultaneousExerciceDone(movementList: ArrayList<Movement>): Boolean {
        var repetitionDone = true
        movementList.forEach()
        {
            if (it.movementState != MovementState.WAITING_FOR_OTHER_MOVEMENT_ENDING_ANGLE) {
                repetitionDone = false
            }
        }
        return repetitionDone
    }

    //Calculates the angle between the three points in a movement
    fun calculateAngleV2(movement: Movement, drawView: DrawView) {
        var pointX0: Float = drawView.mDrawPoint[movement.bodyPart0_Index].x
        var pointY0: Float = drawView.mDrawPoint[movement.bodyPart0_Index].y
        var pointX1: Float = drawView.mDrawPoint[movement.bodyPart1_Index].x
        var pointY1: Float = drawView.mDrawPoint[movement.bodyPart1_Index].y
        var pointX2: Float = drawView.mDrawPoint[movement.bodyPart2_Index].x
        var pointY2: Float = drawView.mDrawPoint[movement.bodyPart2_Index].y

        var X1ToX0: Float = pointX0 - pointX1
        var Y1ToY0: Float = pointY0 - pointY1
        var X1ToX2: Float = pointX2 - pointX1
        var Y1ToY2: Float = pointY2 - pointY1

        var X1X0mod: Float = sqrt((X1ToX0 * X1ToX0) + (Y1ToY0 * Y1ToY0))
        var X1X2mod: Float = sqrt((X1ToX2 * X1ToX2) + (Y1ToY2 * Y1ToY2))

        var vectorProduct: Float = X1ToX0 * X1ToX2 + Y1ToY0 * Y1ToY2

        var angleRad: Float = acos(vectorProduct / (X1X0mod * X1X2mod))
        var angleDeg: Double = ((angleRad * 180) / Math.PI)

        //Adding anti/clockwise effect
        var a = Y1ToY0 / X1ToX0
        var b = pointY0 - (a * pointX0)
        var tmpPointY2 = (a * pointX2) + b
        if (movement.isAngleAntiClockWise!!) {
            if (tmpPointY2 < pointY2) {
                angleDeg = 360 - angleDeg
            }
        } else {
            if (tmpPointY2 > pointY2) {
                angleDeg = 360 - angleDeg
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
    fun isAngleMatching(movement: Movement): Boolean {
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

    fun copy(): Exercice {
        val exercices = Exercice()
        exercices.maxExecutionTime = maxExecutionTime
        exercices.minExecutionTime = minExecutionTime
        exercices.mouvementStartTimer = mouvementStartTimer
        exercices.mouvementSpeedTime = mouvementSpeedTime
        exercices.numberOfRepetitionToDo = numberOfRepetitionToDo
        exercices.numberOfRepetition = numberOfRepetition
        exercices.exitStateReached = exitStateReached
        exercices.exerciceEndTime = exerciceEndTime

        exercices.bp.HEAD.X = bp.HEAD.X
        exercices.bp.HEAD.Y = bp.HEAD.Y
        exercices.bp.NECK.X = bp.NECK.X
        exercices.bp.NECK.Y = bp.NECK.Y
        exercices.bp.L_SHOULDER.X = bp.L_SHOULDER.X
        exercices.bp.L_SHOULDER.Y = bp.L_SHOULDER.Y
        exercices.bp.L_ELBOW.X = bp.L_ELBOW.X
        exercices.bp.L_ELBOW.Y = bp.L_ELBOW.Y
        exercices.bp.L_WRIST.X = bp.L_WRIST.X
        exercices.bp.L_WRIST.Y = bp.L_WRIST.Y
        exercices.bp.R_SHOULDER.X = bp.R_SHOULDER.X
        exercices.bp.R_SHOULDER.Y = bp.R_SHOULDER.Y
        exercices.bp.R_ELBOW.X = bp.R_ELBOW.X
        exercices.bp.R_ELBOW.Y = bp.R_ELBOW.Y
        exercices.bp.R_WRIST.X = bp.R_WRIST.X
        exercices.bp.R_WRIST.Y = bp.R_WRIST.Y
        exercices.bp.L_HIP.X = bp.L_HIP.X
        exercices.bp.L_HIP.Y = bp.L_HIP.Y
        exercices.bp.L_KNEE.X = bp.L_KNEE.X
        exercices.bp.L_KNEE.Y = bp.L_KNEE.Y
        exercices.bp.L_ANKLE.X = bp.L_ANKLE.X
        exercices.bp.L_ANKLE.Y = bp.L_ANKLE.Y
        exercices.bp.R_HIP.X = bp.R_HIP.X
        exercices.bp.R_HIP.Y = bp.R_HIP.Y
        exercices.bp.R_KNEE.X = bp.R_KNEE.X
        exercices.bp.R_KNEE.Y = bp.R_KNEE.Y
        exercices.bp.R_ANKLE.X = bp.R_ANKLE.X
        exercices.bp.R_ANKLE.Y = bp.R_ANKLE.Y

        movementList.forEach() {
            var tmpMovement = Movement(it.bodyPart0_Index, it.bodyPart1_Index, it.bodyPart2_Index)
            tmpMovement.startingAngle = it.startingAngle
            tmpMovement.endingAngle = it.endingAngle
            tmpMovement.isAngleAntiClockWise = it.isAngleAntiClockWise
            tmpMovement.angleAvg = it.angleAvg
            tmpMovement.member1Length = it.member1Length
            tmpMovement.member2Length = it.member2Length
            tmpMovement.angleOffset = it.angleOffset
            tmpMovement.movementState = it.movementState
            tmpMovement.member1LengthLastFrames = it.member1LengthLastFrames
            tmpMovement.member2LengthLastFrames = it.member2LengthLastFrames
            tmpMovement.angleOffsetLastFrames = it.angleOffsetLastFrames
            tmpMovement.angleValuesLastFrames = it.angleValuesLastFrames

            exercices.movementList.add(tmpMovement)
        }

        exercices.initList = initList
        exercices.notMovingInitList = notMovingInitList
        exercices.isInit = isInit
        exercices.exerciceStartTime = exerciceStartTime
        exercices.notMovingStartTime = notMovingStartTime
        exercices.notMovingTimer = notMovingTimer
        exercices.initStartTimer = initStartTimer
        exercices.targetTime = targetTime
        exercices.timeStamp = timeStamp

        return exercices
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
        parcel.writeSerializable(exerciceType)
        parcel.writeValue(chronoTime)
        parcel.writeValue(allowedTimeForExercice)
        parcel.writeValue(exerciceStartTime)
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

    companion object CREATOR : Parcelable.Creator<Exercice> {
        override fun createFromParcel(parcel: Parcel): Exercice {
            return Exercice(parcel)
        }

        override fun newArray(size: Int): Array<Exercice?> {
            return arrayOfNulls(size)
        }
    }
}


