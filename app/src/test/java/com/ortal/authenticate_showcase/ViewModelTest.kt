package com.ortal.authenticate_showcase

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.*
import com.ortal.authenticate_showcase.data.model.LoggedInUserView
import com.ortal.authenticate_showcase.data.model.LoginFormState
import com.ortal.authenticate_showcase.data.model.LoginResult
import com.ortal.authenticate_showcase.domain.usecase.LogInWithTokenUseCase
import com.ortal.authenticate_showcase.domain.usecase.LogInWithUserPasswordUseCase
import com.ortal.authenticate_showcase.ui.login.LoginViewModel
import com.ortal.authenticate_showcase.util.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
internal class ViewModelTest {

    private lateinit var loginViewModel: LoginViewModel
    private val testDispatcher = TestCoroutineDispatcher()

    private val logInWithUserPasswordUseCase: LogInWithUserPasswordUseCase = mock()
    private val logInWithTokenUseCase: LogInWithTokenUseCase = mock()

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    @Before
    fun setUp() {
        Dispatchers.resetMain()
        Dispatchers.setMain(testDispatcher)
        loginViewModel = LoginViewModel(logInWithUserPasswordUseCase, logInWithTokenUseCase)

    }

    @Test
    internal fun `Verify call, only once, login with token from logInWithTokenUseCase at invoke`() =
        runBlockingTest {

            loginViewModel.loginWithToken("token")

            verify(logInWithTokenUseCase, times(1)).invoke(any())
            verify(logInWithUserPasswordUseCase, times(0)).invoke(any(), any())

        }

    @Test
    internal fun `Verify call, only once, login with username and password from logInWithUserPasswordUseCase at invoke`() =
        runBlockingTest {

            loginViewModel.loginWithUsernamePassword("username", "password")

            verify(logInWithUserPasswordUseCase, times(1)).invoke(any(), any())
            verify(logInWithTokenUseCase, times(0)).invoke(any())

        }

    @Test
    internal
    fun `Verify UI model is correct with logged in user with username and password`() =
        runBlockingTest {
            whenever(logInWithUserPasswordUseCase.invoke("username", "password")).thenReturn(
                LoginResult(LoggedInUserView("name", "token", true), null)
            )


            loginViewModel.loginWithUsernamePassword("username", "password")


            val result = loginViewModel.loginResult.getOrAwaitValue()

            val expectedResult = LoginResult(success = LoggedInUserView("name", "token", true))

            assertEquals(expectedResult, result)

        }

    @Test
    internal fun `Verify UI model is correct with logged in user with token`() =
        runBlockingTest {
            whenever(logInWithTokenUseCase.invoke("token")).thenReturn(
                LoginResult(LoggedInUserView("name", "token", false), null)
            )


            loginViewModel.loginWithToken("token")


            val result = loginViewModel.loginResult.getOrAwaitValue()

            val expectedResult = LoginResult(success = LoggedInUserView("name", "token", false))

            assertEquals(expectedResult, result)

        }

    @Test
    internal fun `Verify UI model is correct when login data changed with valid request`() =
        runBlockingTest {

            loginViewModel.loginDataChanged("username", "password")


            val result = loginViewModel.loginFormState.getOrAwaitValue()

            val expectedResult =
                LoginFormState(usernameError = null, passwordError = null, isDataValid = true)

            assertEquals(expectedResult, result)

        }

    @Test
    internal fun `Verify UI model is correct when login data changed with short password`() =
        runBlockingTest {

            loginViewModel.loginDataChanged("username", "pass")


            val result = loginViewModel.loginFormState.getOrAwaitValue()

            assertEquals(false, result.isDataValid)
            assert(result.passwordError is Int)
            assertEquals(null, result.usernameError)

        }


    @Test
    internal fun `Verify UI model is correct when login data changed with empty username`() =
        runBlockingTest {

            loginViewModel.loginDataChanged("", "password")


            val result = loginViewModel.loginFormState.getOrAwaitValue()

            assertEquals(false, result.isDataValid)
            assertEquals(null, result.passwordError)
            assert(result.usernameError is Int)

        }


}