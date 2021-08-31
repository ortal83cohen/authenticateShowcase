package com.ortal.authenticate_showcase.domain.usecase


import com.ortal.authenticate_showcase.data.LoginRepository
import com.ortal.authenticate_showcase.data.model.LoginResult
import com.ortal.authenticate_showcase.domain.mapper.LogInFromRemoteMapper


class LogInWithUserPasswordUseCase constructor(
    private val loginRepository: LoginRepository
) {
    suspend operator fun invoke(
        username: String, password: String
    ): LoginResult {
        return LogInFromRemoteMapper(
            loginRepository.login(
                username, password
            )
        )
    }
}