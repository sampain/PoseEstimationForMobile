package com.epmus.mobile.program

import android.os.Parcel
import android.os.Parcelable
import com.epmus.mobile.poseestimation.Exercice

class ExerciceData() : Parcelable {
    var id: String = ""
    var name: String = ""
    var description: String = ""
    var imagePath: String = ""
    var exercice: Exercice = Exercice()

    constructor(parcel: Parcel) : this() {
        id = parcel.readString().toString()
        name = parcel.readString().toString()
        description = parcel.readString().toString()
        exercice = parcel.readParcelable(Exercice::class.java.classLoader)!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(description)
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