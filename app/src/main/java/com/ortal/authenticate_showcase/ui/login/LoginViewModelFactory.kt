package com.ortal.authenticate_showcase.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ortal.authenticate_showcase.data.LocalLoginDataSource
import com.ortal.authenticate_showcase.data.LoginRepositoryImp
import com.ortal.authenticate_showcase.data.RemoteLoginDataSource
import com.ortal.authenticate_showcase.domain.usecase.LogInWithTokenUseCase
import com.ortal.authenticate_showcase.domain.usecase.LogInWithUserPasswordUseCase
import kotlinx.coroutines.Dispatchers

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class LoginViewModelFactory : ViewModelProvider.Factory {

    // TODO: 8/30/2021  get this value form constructor (DI)
    private val dataSourceRemote = RemoteLoginDataSource()
    private val dataSourceLocal = LocalLoginDataSource()


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(
                LogInWithUserPasswordUseCase(
                    LoginRepositoryImp(
                        dataSourceRemote = dataSourceRemote,
                        dataSourceLocal = dataSourceLocal,
                        Dispatchers.IO
                    )
                ), LogInWithTokenUseCase(
                    LoginRepositoryImp(
                        dataSourceRemote = dataSourceRemote,
                        dataSourceLocal = dataSourceLocal,
                        Dispatchers.IO
                    )
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}