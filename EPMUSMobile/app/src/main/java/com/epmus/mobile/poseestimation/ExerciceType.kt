package com.epmus.mobile.poseestimation

import com.epmus.mobile.program.ExerciceNameList
import java.io.Serializable

enum class ExerciceType(val exerciceType: String): Serializable {
    CHRONO("CHRONO"),
    REPETITION("REPETITION"),
    HOLD("HOLD");

    companion object {
        fun getEnumValue(value: String): ExerciceType? =
            ExerciceType.values().find { it.exerciceType == value }
    }
}