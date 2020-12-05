package com.epmus.mobile.Messaging

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MessagingUser(
    val uid: String,
    val nickname: String,
) : Parcelable