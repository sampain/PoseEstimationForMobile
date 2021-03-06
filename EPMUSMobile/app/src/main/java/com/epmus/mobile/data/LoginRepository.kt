package com.epmus.mobile.data

import io.realm.mongodb.User

class LoginRepository(val dataSource: LoginDataSource) {

    var user: User? = null
        private set

    init {
        user = null
    }

    fun login(user: User?): Result<User> {
        val result = dataSource.login(user)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    private fun setLoggedInUser(loggedInUser: User?) {
        this.user = loggedInUser
    }
}