package com.ortal.authenticate_showcase.data

import com.ortal.authenticate_showcase.data.model.LoggedInUser
import com.ortal.authenticate_showcase.data.model.Result

interface LoginRepository {


    suspend fun login(username: String, password: String): Result<LoggedInUser>

    suspend fun login(token: String): Result<LoggedInUser>

}