package com.ortal.authenticate_showcase.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ortal.authenticate_showcase.R
import com.ortal.authenticate_showcase.data.model.LoginFormState
import com.ortal.authenticate_showcase.data.model.LoginResult
import com.ortal.authenticate_showcase.domain.usecase.LogInWithTokenUseCase
import com.ortal.authenticate_showcase.domain.usecase.LogInWithUserPasswordUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(
    private val logInWithUserPasswordUseCase: LogInWithUserPasswordUseCase,
    private val logInWithTokenUseCase: LogInWithTokenUseCase
) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun loginWithUsernamePassword(username: String, password: String) {

        viewModelScope.launch(Dispatchers.IO) {
            _loginResult.postValue(logInWithUserPasswordUseCase(username, password))
        }

    }

    fun loginWithToken(token: String) {

        viewModelScope.launch(Dispatchers.IO) {
            _loginResult.postValue(logInWithTokenUseCase(token))
        }

    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}