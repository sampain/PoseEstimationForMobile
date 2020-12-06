package com.epmus.mobile.poseestimation

import java.io.Serializable

enum class ExerciseType(val exerciseType: String): Serializable {
    CHRONO("CHRONO"),
    REPETITION("REPETITION"),
    HOLD("HOLD"),
    AMPLITUDE("AMPLITUDE");

    companion object {
        fun getEnumValue(value: String): ExerciseType? =
            values().find { it.exerciseType == value }
    }
}

enum class ExerciseTypeUI(val exerciseTypeUI: String): Serializable {
    Chronomètre("CHRONO"),
    Répétition("REPETITION"),
    Maintenir("HOLD"),
    Amplitude("AMPLITUDE");

    companion object {
        fun getEnumValue(value: String): ExerciseTypeUI? =
            values().find { it.exerciseTypeUI == value }
    }
}