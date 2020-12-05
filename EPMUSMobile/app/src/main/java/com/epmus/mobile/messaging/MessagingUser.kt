package com.epmus.mobile.messaging

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MessagingUser(
    val uid: String,
    val nickname: String,
) : Parcelable