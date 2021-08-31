package com.ortal.authenticate_showcase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.ortal.authenticate_showcase.data.LoginRepository
import com.ortal.authenticate_showcase.data.model.Result
import com.ortal.authenticate_showcase.data.model.LoggedInUser
import com.ortal.authenticate_showcase.data.model.LoggedInUserView
import com.ortal.authenticate_showcase.domain.usecase.LogInWithTokenUseCase
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

internal class LogInWithTokenUseCaseTest {

    private lateinit var logInWithTokenUseCase: LogInWithTokenUseCase
    private val testDispatcher = TestCoroutineDispatcher()

    private val loginRepository: LoginRepository = mock()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        logInWithTokenUseCase = LogInWithTokenUseCase(loginRepository)

    }

    @Test
    internal
    fun `Verify call, only once, login with token from loginRepository at invoke`() =
        runBlockingTest {
            whenever(loginRepository.login("token")).thenReturn(
                Result.Success(
                    LoggedInUser(
                        "token",
                        "username"
                    )
                )
            )

            logInWithTokenUseCase("token")


            verify(loginRepository, times(1)).login("token")

        }

    @Test
    internal
    fun `Verify UI model is correct with logged in user`() =
        runBlockingTest {
            whenever(loginRepository.login("token")).thenReturn(
                Result.Success(
                    LoggedInUser(
                        "token",
                        "username"
                    )
                )
            )

            val user = logInWithTokenUseCase("token")

            val expectedResult = LoggedInUserView("username", "token", false)

            assertEquals(expectedResult, user.success)
            assertEquals(null, user.error)
        }

    @Test
    internal
    fun `Verify UI model is correct with failed logged in`() =
        runBlockingTest {
            whenever(loginRepository.login("token")).thenReturn(Result.Error(IOException("some error")))

            val user = logInWithTokenUseCase("token")

            assertEquals(null, user.success)
            assert(user.error is Int)
        }

}