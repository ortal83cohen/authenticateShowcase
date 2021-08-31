package com.ortal.authenticate_showcase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.ortal.authenticate_showcase.data.*
import com.ortal.authenticate_showcase.data.model.LoggedInUser
import com.ortal.authenticate_showcase.data.model.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import java.io.IOException

@InternalCoroutinesApi
@ExperimentalCoroutinesApi

internal class LogInRepositoryTest {

    private lateinit var loginRepository: LoginRepository
    private val testDispatcher = TestCoroutineDispatcher()

    private val remoteLoginDataSource: RemoteLoginDataSource = mock()
    private val localLoginDataSource: LocalLoginDataSource = mock()


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        loginRepository =
            LoginRepositoryImp(remoteLoginDataSource, localLoginDataSource, testDispatcher)

    }

    @Test
    internal
    fun `Verify call, only once, login with user and password from remoteLoginDataSource at invoke`() =
        runBlockingTest {

            whenever(remoteLoginDataSource.login("user", "password")).thenReturn(
                Result.Success(
                    LoggedInUser(
                        "token",
                        "username"
                    )
                )
            )

            loginRepository.login("user", "password")


            verify(remoteLoginDataSource, times(1)).login("user", "password")

        }

    @Test
    internal
    fun `Verify Result is correct with logged in user`() =
        runBlockingTest {
            val expectedResult = Result.Success(
                LoggedInUser(
                    "username",
                    "password"
                )
            )

            whenever(
                remoteLoginDataSource.login(
                    "username",
                    "password"
                )
            ).thenReturn(
                expectedResult
            )

            val result = loginRepository.login(
                "username",
                "password"
            )



            assert(result is Result.Success)
            assert(result == expectedResult)

        }

    @Test
    internal
    fun `Verify Result is correct with failed logged in`() =
        runBlockingTest {

            val expectedResult = Result.Error(IOException("some error"))


            whenever(
                remoteLoginDataSource.login(
                    "username",
                    "password"
                )
            ).thenReturn(expectedResult)

            val result = loginRepository.login(
                "username",
                "password"
            )


            assert(result is Result.Error)
            assert(result == expectedResult)
        }

    @Test
    internal
    fun `Verify call, only once, login with token from localLoginDataSource at invoke`() =
        runBlockingTest {

            whenever(localLoginDataSource.login("token")).thenReturn(
                Result.Success(
                    LoggedInUser(
                        "token",
                        "username"
                    )
                )
            )

            loginRepository.login("token")


            verify(localLoginDataSource, times(1)).login("token")

        }

    @Test
    internal
    fun `Verify Result is correct with token logged in user`() =
        runBlockingTest {
            val expectedResult = Result.Success(LoggedInUser("token", "username"))

            whenever(
                localLoginDataSource.login("token")
            ).thenReturn(
                expectedResult
            )

            val result = loginRepository.login("token")



            assert(result is Result.Success)
            assert(result == expectedResult)

        }

    @Test
    internal
    fun `Verify Result is correct with token failed logged in`() =
        runBlockingTest {

            val expectedResult = Result.Error(IOException("some error"))


            whenever(
                localLoginDataSource.login("token")
            ).thenReturn(expectedResult)

            val result = loginRepository.login(
                "token"
            )


            assert(result is Result.Error)
            assert(result == expectedResult)
        }

}