package com.epmus.mobile.data

import io.realm.mongodb.User

sealed class Result<out T : Any> {

    data class Success<out T : Any>(val data: User?) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }
}