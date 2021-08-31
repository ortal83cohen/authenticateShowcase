package com.ortal.authenticate_showcase.domain.usecase


import com.ortal.authenticate_showcase.data.LoginRepository
import com.ortal.authenticate_showcase.data.model.LoginResult
import com.ortal.authenticate_showcase.domain.mapper.LogInFromLocalMapper


class LogInWithTokenUseCase constructor(
    private val loginRepository: LoginRepository
) {
    suspend operator fun invoke(
        token: String
    ): LoginResult {
        return LogInFromLocalMapper(
            loginRepository.login(
                token
            )
        )
    }
}