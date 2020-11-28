package com.epmus.mobile.poseestimation

import java.io.Serializable

enum class ExerciceType(val exerciceType: String): Serializable {
    CHRONO("CHRONO"),
    REPETITION("REPETITION"),
    HOLD("HOLD");

    companion object {
        fun getEnumValue(value: String): ExerciceType? =
            values().find { it.exerciceType == value }
    }
}

enum class ExerciceTypeUI(val exerciceTypeUI: String): Serializable {
    Chronomètre("CHRONO"),
    Répétition("REPETITION"),
    Maintenir("HOLD");

    companion object {
        fun getEnumValue(value: String): ExerciceTypeUI? =
            values().find { it.exerciceTypeUI == value }
    }
}