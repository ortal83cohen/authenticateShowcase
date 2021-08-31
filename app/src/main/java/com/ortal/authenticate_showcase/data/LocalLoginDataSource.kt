package com.ortal.authenticate_showcase.data

import com.ortal.authenticate_showcase.data.model.LoggedInUser
import com.ortal.authenticate_showcase.data.model.Result
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LocalLoginDataSource {

    // TODO: 8/30/2021 load the user from DB with the token

    fun login(token: String): Result<LoggedInUser> {
        return try {
            // TODO: handle loggedInUser authentication
            val fakeUser = LoggedInUser(token, "username")
            Result.Success(fakeUser)
        } catch (e: Throwable) {
            Result.Error(IOException("Error logging in", e))
        }
    }

}