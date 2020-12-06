package com.epmus.mobile.program

import android.os.Parcel
import android.os.Parcelable
import com.epmus.mobile.poseestimation.Exercise

class ExerciseData() : Parcelable {
    var id: String = ""
    var name: String = ""
    var description: String = ""
    var imagePath: String = ""
    var exercise: Exercise = Exercise()
    var mondayAlarm : Boolean = false
    var tuesdayAlarm : Boolean = false
    var wednesdayAlarm : Boolean = false
    var thursdayAlarm : Boolean = false
    var fridayAlarm : Boolean = false
    var saturdayAlarm : Boolean = false
    var sundayAlarm : Boolean = false

    constructor(parcel: Parcel) : this() {
        id = parcel.readString().toString()
        name = parcel.readString().toString()
        description = parcel.readString().toString()
        exercise = parcel.readParcelable(Exercise::class.java.classLoader)!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeParcelable(exercise, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ExerciseData> {
        override fun createFromParcel(parcel: Parcel): ExerciseData {
            return ExerciseData(parcel)
        }

        override fun newArray(size: Int): Array<ExerciseData?> {
            return arrayOfNulls(size)
        }
    }
}