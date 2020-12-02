package com.epmus.mobile.poseestimation

enum class BodyPart(val bodyPart: String) {
    HEAD("HEAD"),
    NECK("NECK"),
    L_SHOULDER("L_SHOULDER"),
    L_ELBOW("L_ELBOW"),
    L_WRIST("L_WRIST"),
    R_SHOULDER("R_SHOULDER"),
    R_ELBOW("R_ELBOW"),
    R_WRIST("R_WRIST"),
    L_HIP("L_HIP"),
    L_KNEE("L_KNEE"),
    L_ANKLE("L_ANKLE"),
    R_HIP("R_HIP"),
    R_KNEE("R_KNEE"),
    R_ANKLE("R_ANKLE"),
    HIP("HIP");

    companion object {
        fun getEnumValue(value: String): BodyPart? =
            values().find { it.bodyPart == value }
    }
}