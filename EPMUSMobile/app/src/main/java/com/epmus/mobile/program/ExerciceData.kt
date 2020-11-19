package com.epmus.mobile.program

import android.os.Parcel
import android.os.Parcelable
import com.epmus.mobile.poseestimation.BodyPart
import com.epmus.mobile.poseestimation.Exercice
import com.epmus.mobile.poseestimation.ExerciceType
import com.epmus.mobile.poseestimation.Movement

class ExerciceData() : Parcelable {
    var name: String = ""
    var exercice: Exercice = Exercice()

    constructor(parcel: Parcel) : this() {
        name = parcel.readString().toString()
        exercice = parcel.readParcelable(Exercice::class.java.classLoader)!!
    }


    fun getExerciceData(exerciceName: ExerciceNameList?): ExerciceData {

        var exercice = Exercice()

        var movement = Movement(
            BodyPart.L_SHOULDER.ordinal,
            BodyPart.L_ELBOW.ordinal,
            BodyPart.L_WRIST.ordinal
        )
        var movement2 = Movement(
            BodyPart.R_SHOULDER.ordinal,
            BodyPart.R_ELBOW.ordinal,
            BodyPart.R_WRIST.ordinal
        )

        exercice.minExecutionTime = 1.0f
        exercice.maxExecutionTime = 3.0f

            if (exerciceName == ExerciceNameList.ExerciceBrasRepetition) {
                movement.startingAngle = 180
                movement.endingAngle = 90
                movement.isAngleClockWise = false
                movement2.startingAngle = 180
                movement2.endingAngle = 270
                movement2.isAngleClockWise = true
                exercice.movementList.add(movement)
                exercice.movementList.add(movement2)
                exercice.numberOfRepetitionToDo = 5
                exercice.exerciceType = ExerciceType.REPETITION
            } else if (exerciceName == ExerciceNameList.ExerciceBrasGaucheRepetition) {
                movement.startingAngle = 170
                movement.endingAngle = 90
                movement.isAngleClockWise = false
                exercice.movementList.add(movement)
                exercice.numberOfRepetitionToDo = 5
                exercice.exerciceType = ExerciceType.REPETITION
            } else if (exerciceName == ExerciceNameList.ExerciceBrasDroitRepetition) {
                movement2.startingAngle = 170
                movement2.endingAngle = 90
                movement2.isAngleClockWise = true
                exercice.movementList.add(movement2)
                exercice.numberOfRepetitionToDo = 5
                exercice.exerciceType = ExerciceType.REPETITION
            } else if (exerciceName == ExerciceNameList.ExerciceBrasChrono) {
                movement.startingAngle = 180
                movement.endingAngle = 90
                movement.isAngleClockWise = false
                movement2.startingAngle = 180
                movement2.endingAngle = 270
                movement2.isAngleClockWise = true
                exercice.movementList.add(movement)
                exercice.movementList.add(movement2)
                exercice.exerciceType = ExerciceType.CHRONO
                exercice.allowedTimeForExercice = 15
            } else if (exerciceName == ExerciceNameList.ExerciceBrasGaucheChrono) {
                movement.startingAngle = 170
                movement.endingAngle = 90
                movement.isAngleClockWise = false
                exercice.movementList.add(movement)
                exercice.exerciceType = ExerciceType.CHRONO
                exercice.allowedTimeForExercice = 15
            } else if (exerciceName == ExerciceNameList.ExerciceBrasDroitChrono) {
                movement2.startingAngle = 180
                movement2.endingAngle = 270
                movement2.isAngleClockWise = true
                exercice.movementList.add(movement2)
                exercice.exerciceType = ExerciceType.CHRONO
                exercice.allowedTimeForExercice = 15
            } else if (exerciceName == ExerciceNameList.ExerciceBrasHold) {
                movement.startingAngle = 180
                movement.endingAngle = 90
                movement.isAngleClockWise = false
                movement2.startingAngle = 180
                movement2.endingAngle = 270
                movement2.isAngleClockWise = true
                exercice.movementList.add(movement)
                exercice.movementList.add(movement2)
                exercice.exerciceType = ExerciceType.HOLD
                exercice.targetHoldTime = 10
            } else if (exerciceName == ExerciceNameList.ExerciceBrasGaucheHold) {
                movement.startingAngle = 170
                movement.endingAngle = 90
                movement.isAngleClockWise = false
                exercice.movementList.add(movement)
                exercice.exerciceType = ExerciceType.HOLD
                exercice.targetHoldTime = 10
            } else if (exerciceName == ExerciceNameList.ExerciceBrasDroitHold) {
                movement2.startingAngle = 180
                movement2.endingAngle = 270
                movement2.isAngleClockWise = true
                exercice.movementList.add(movement2)
                exercice.exerciceType = ExerciceType.HOLD
                exercice.targetHoldTime = 10
            } else if (exerciceName == ExerciceNameList.ExerciceBrasGaucheAmplitude) {
                movement.startingAngle = 180
                movement.endingAngle = 90
                movement.isAngleClockWise = false
                exercice.movementList.add(movement)
                exercice.exerciceType = ExerciceType.AMPLITUDE
            }


        if (exerciceName == ExerciceNameList.ExerciceBrasRepetition) {
            movement.startingAngle = 180
            movement.endingAngle = 90
            movement.isAngleAntiClockWise = true
            movement2.startingAngle = 180
            movement2.endingAngle = 270
            movement2.isAngleAntiClockWise = false
            exercice.movementList.add(movement)
            exercice.movementList.add(movement2)
            exercice.numberOfRepetitionToDo = 5
            exercice.exerciceType = ExerciceType.REPETITION
        } else if (exerciceName == ExerciceNameList.ExerciceBrasGaucheRepetition) {
            movement.startingAngle = 170
            movement.endingAngle = 90
            movement.isAngleAntiClockWise = true
            exercice.movementList.add(movement)
            exercice.numberOfRepetitionToDo = 5
            exercice.exerciceType = ExerciceType.REPETITION
        } else if (exerciceName == ExerciceNameList.ExerciceBrasDroitRepetition) {
            movement2.startingAngle = 180
            movement2.endingAngle = 270
            movement2.isAngleAntiClockWise = false
            exercice.movementList.add(movement2)
            exercice.numberOfRepetitionToDo = 5
            exercice.exerciceType = ExerciceType.REPETITION
        } else if (exerciceName == ExerciceNameList.ExerciceBrasChrono) {
            movement.startingAngle = 180
            movement.endingAngle = 90
            movement.isAngleAntiClockWise = true
            movement2.startingAngle = 180
            movement2.endingAngle = 270
            movement2.isAngleAntiClockWise = false
            exercice.movementList.add(movement)
            exercice.movementList.add(movement2)
            exercice.exerciceType = ExerciceType.CHRONO
            exercice.allowedTimeForExercice = 15
        } else if (exerciceName == ExerciceNameList.ExerciceBrasGaucheChrono) {
            movement.startingAngle = 170
            movement.endingAngle = 90
            movement.isAngleAntiClockWise = true
            exercice.movementList.add(movement)
            exercice.exerciceType = ExerciceType.CHRONO
            exercice.allowedTimeForExercice = 15
        } else if (exerciceName == ExerciceNameList.ExerciceBrasDroitChrono) {
            movement2.startingAngle = 180
            movement2.endingAngle = 270
            movement2.isAngleAntiClockWise = false
            exercice.movementList.add(movement2)
            exercice.exerciceType = ExerciceType.CHRONO
            exercice.allowedTimeForExercice = 15
        } else if (exerciceName == ExerciceNameList.ExerciceBrasHold) {
            movement.startingAngle = 180
            movement.endingAngle = 90
            movement.isAngleAntiClockWise = true
            movement2.startingAngle = 180
            movement2.endingAngle = 270
            movement2.isAngleAntiClockWise = false
            exercice.movementList.add(movement)
            exercice.movementList.add(movement2)
            exercice.exerciceType = ExerciceType.HOLD
            exercice.targetHoldTime = 10
        } else if (exerciceName == ExerciceNameList.ExerciceBrasGaucheHold) {
            movement.startingAngle = 170
            movement.endingAngle = 90
            movement.isAngleAntiClockWise = true
            exercice.movementList.add(movement)
            exercice.exerciceType = ExerciceType.HOLD
            exercice.targetHoldTime = 10
        } else if (exerciceName == ExerciceNameList.ExerciceBrasDroitHold) {
            movement2.startingAngle = 180
            movement2.endingAngle = 270
            movement2.isAngleAntiClockWise = false
            exercice.movementList.add(movement2)
            exercice.exerciceType = ExerciceType.HOLD
            exercice.targetHoldTime = 10
        }

        var exerciceData: ExerciceData = ExerciceData()
        exerciceData.exercice = exercice
        exerciceData.name = exerciceName?.exerciceName!!

        return exerciceData
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeParcelable(exercice, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ExerciceData> {
        override fun createFromParcel(parcel: Parcel): ExerciceData {
            return ExerciceData(parcel)
        }

        override fun newArray(size: Int): Array<ExerciceData?> {
            return arrayOfNulls(size)
        }
    }
}

enum class ExerciceNameList(val exerciceName: String) {
    ExerciceBrasGaucheRepetition("Exercice Bras Gauche Repetition"),
    ExerciceBrasDroitRepetition("Exercice Bras Droit Repetition"),
    ExerciceBrasRepetition("Exercice Bras Repetition"),
    ExerciceBrasGaucheChrono("Exercice Bras Gauche Chrono"),
    ExerciceBrasDroitChrono("Exercice Bras Droit Chrono"),
    ExerciceBrasChrono("Exercice Bras Chrono"),
    ExerciceBrasGaucheHold("Exercice Bras Gauche Hold"),
    ExerciceBrasDroitHold("Exercice Bras Droit Hold"),
    ExerciceBrasHold("Exercice Bras Hold"),
    ExerciceBrasGaucheAmplitude("Exercice Bras Gauche Amplitude");

    companion object {
        fun getEnumValue(value: String): ExerciceNameList? =
            values().find { it.exerciceName == value }
    }
}

/*enum class ExerciceList(val exerciceName: String, val exercice: ExerciceData) {
    ExerciceBrasGauche(ExerciceBrasGauche.exerciceName, getExerciceData(ExerciceNameList.ExerciceBrasGauche)),
    ExerciceBrasDroit(ExerciceBrasDroit.exerciceName, getExerciceData(ExerciceNameList.ExerciceBrasDroit)),
    ExerciceBras(ExerciceBras.exerciceName, getExerciceData(ExerciceNameList.ExerciceBras))
}*/