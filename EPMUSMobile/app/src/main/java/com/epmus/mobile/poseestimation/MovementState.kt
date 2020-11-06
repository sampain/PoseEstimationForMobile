package com.epmus.mobile.poseestimation

import java.io.Serializable

enum class MovementState: Serializable {
    INIT,
    STARTING_ANGLE_REACHED,
    ENDING_ANGLE_REACHED,
    WAITING_FOR_OTHER_MOVEMENT_ENDING_ANGLE,
    WAITING_FOR_OTHER_MOVEMENT_STARTING_ANGLE,
}