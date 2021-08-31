package com.ortal.authenticate_showcase.data

import com.ortal.authenticate_showcase.data.model.LoggedInUser
import com.ortal.authenticate_showcase.data.model.Result
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepositoryImp(
    private val dataSourceRemote: RemoteLoginDataSource,
    private val dataSourceLocal: LocalLoginDataSource,
    private val dispatchers: CoroutineContext
) : LoginRepository {

    // in-memory cache of the loggedInUser object
    // TODO: 8/30/2021  implement one source of true from persistent Data source
    var user: LoggedInUser? = null
        private set

    override suspend fun login(username: String, password: String): Result<LoggedInUser> {

        return withContext(dispatchers) {
            val result = dataSourceRemote.login(username, password)

            if (result is Result.Success) {
                setLoggedInUser(result.data)
            }

            result
        }
    }

    override suspend fun login(token: String): Result<LoggedInUser> {

        return withContext(dispatchers) {
            val result = dataSourceLocal.login(token)

            if (result is Result.Success) {
                setLoggedInUser(result.data)
            }

            result
        }
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user =
            loggedInUser   // If user credentials will be cached in local storage, it should be encrypted
    }
}