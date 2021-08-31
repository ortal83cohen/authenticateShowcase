package com.ortal.authenticate_showcase.data

import com.ortal.authenticate_showcase.data.model.LoggedInUser
import com.ortal.authenticate_showcase.data.model.Result
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class RemoteLoginDataSource {
// TODO: 8/30/2021 implement authentication API

    fun login(username: String, password: String): Result<LoggedInUser> {
        return try {
            // TODO: handle loggedInUser authentication
            val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), username)
            Result.Success(fakeUser)
        } catch (e: Throwable) {
            Result.Error(IOException("Error logging in", e))
        }
    }

}