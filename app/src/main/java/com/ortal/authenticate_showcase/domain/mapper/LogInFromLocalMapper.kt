package com.ortal.authenticate_showcase.domain.mapper

import com.ortal.authenticate_showcase.R
import com.ortal.authenticate_showcase.data.model.Result
import com.ortal.authenticate_showcase.data.model.LoggedInUser
import com.ortal.authenticate_showcase.data.model.LoggedInUserView
import com.ortal.authenticate_showcase.data.model.LoginResult

object LogInFromLocalMapper {

    operator fun invoke(result: Result<LoggedInUser>): LoginResult {
        return if (result is Result.Success) {
            LoginResult(
                success = LoggedInUserView(
                    displayName = result.data.displayName,
                    result.data.token,
                    false
                )
            )

        } else {
            LoginResult(error = R.string.login_failed)
        }
    }
}