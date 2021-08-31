package com.ortal.authenticate_showcase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.ortal.authenticate_showcase.data.LoginRepository
import com.ortal.authenticate_showcase.data.model.Result
import com.ortal.authenticate_showcase.data.model.LoggedInUser
import com.ortal.authenticate_showcase.data.model.LoggedInUserView
import com.ortal.authenticate_showcase.domain.usecase.LogInWithUserPasswordUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException

@InternalCoroutinesApi
@ExperimentalCoroutinesApi

internal class LogInWithUserPasswordUseCaseTest {

    private lateinit var logInWithUserPasswordUseCase: LogInWithUserPasswordUseCase
    private val testDispatcher = TestCoroutineDispatcher()

    private val loginRepository: LoginRepository = mock()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        logInWithUserPasswordUseCase = LogInWithUserPasswordUseCase(loginRepository)
    }

    @Test
    internal
    fun `Verify call, only once, login with token from loginRepository at invoke`() =
        runBlockingTest {
            whenever(loginRepository.login("user", "password")).thenReturn(
                Result.Success(
                    LoggedInUser(
                        "token",
                        "username"
                    )
                )
            )

            logInWithUserPasswordUseCase("user", "password")


            verify(loginRepository, times(1)).login("user", "password")

        }

    @Test
    internal
    fun `Verify UI model is correct with logged in user`() =
        runBlockingTest {
            whenever(loginRepository.login("user", "password")).thenReturn(
                Result.Success(
                    LoggedInUser(
                        "token",
                        "username"
                    )
                )
            )

            val user = logInWithUserPasswordUseCase("user", "password")

            val expectedResult = LoggedInUserView("username", "token", true)

            assertEquals(expectedResult, user.success)
            assertEquals(null, user.error)
        }

    @Test
    internal
    fun `Verify UI model is correct with failed logged in`() =
        runBlockingTest {
            whenever(loginRepository.login("user", "password")).thenReturn(
                Result.Error(
                    IOException(
                        "some error"
                    )
                )
            )

            val user = logInWithUserPasswordUseCase("user", "password")

            assertEquals(null, user.success)
            assert(user.error is Int)
        }

}