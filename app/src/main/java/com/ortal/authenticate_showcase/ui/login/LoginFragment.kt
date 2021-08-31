package com.ortal.authenticate_showcase.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ortal.authenticate_showcase.R
import com.ortal.authenticate_showcase.data.model.LoggedInUserView
import com.ortal.authenticate_showcase.extension.afterTextChanged
import com.ortal.authenticate_showcase.ui.home.HomePageActivity
import com.ortal.authenticate_showcase.ui.login.biometric.BiometricPromptUtils
import com.ortal.authenticate_showcase.ui.login.biometric.CIPHERTEXT_WRAPPER
import com.ortal.authenticate_showcase.ui.login.biometric.CryptographyManager
import com.ortal.authenticate_showcase.ui.login.biometric.SHARED_PREFS_FILENAME
import kotlinx.android.synthetic.main.fragment_login.*
import java.util.*


class LoginFragment : Fragment() {
    private lateinit var biometricPrompt: BiometricPrompt
    private val cryptographyManager = CryptographyManager()
    private val ciphertextWrapper
        get() = cryptographyManager.getCiphertextWrapperFromSharedPrefs(
            requireContext(),
            SHARED_PREFS_FILENAME,
            Context.MODE_PRIVATE,
            CIPHERTEXT_WRAPPER
        )

    companion object {
        fun newInstance() = LoginFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(viewLifecycleOwner, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(viewLifecycleOwner, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
                if (loginResult.success.isFromRemote) {
                    showBiometricPromptForEncryption(loginResult.success.token)
                } else {
                    navigateToHomePage()
                }
            }

        })


        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.loginWithUsernamePassword(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.loginWithUsernamePassword(
                    username.text.toString(),
                    password.text.toString()
                )
            }
        }


        val canAuthenticate =
            BiometricManager.from(requireContext()).canAuthenticate(BIOMETRIC_WEAK)
        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {

            if (ciphertextWrapper != null) {
                fingerprint.visibility = View.VISIBLE
                fingerprint.setOnClickListener {
                    showBiometricPromptForDecryption()
                }

            } else {
                fingerprint.visibility = View.INVISIBLE
            }


        } else {
            fingerprint.visibility = View.INVISIBLE
        }
    }


    private fun showBiometricPromptForDecryption() {
        ciphertextWrapper?.let { textWrapper ->
            val secretKeyName = getString(R.string.secret_key_name)
            val cipher = cryptographyManager.getInitializedCipherForDecryption(
                secretKeyName, textWrapper.initializationVector
            )
            biometricPrompt =
                BiometricPromptUtils.createBiometricPrompt(
                    requireActivity() as AppCompatActivity,
                    ::decryptServerTokenFromStorage
                ) {

                }
            val promptInfo =
                BiometricPromptUtils.createPromptInfoForDecryption(requireActivity() as AppCompatActivity)
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        }
    }

    private fun showBiometricPromptForEncryption(token: String) {
        val canAuthenticate =
            BiometricManager.from(requireActivity() as AppCompatActivity).canAuthenticate()
        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            val secretKeyName = getString(R.string.secret_key_name)
            val cipher = cryptographyManager.getInitializedCipherForEncryption(secretKeyName)
            val biometricPrompt =
                BiometricPromptUtils.createBiometricPrompt(
                    requireActivity() as AppCompatActivity,
                    processSuccess = { authenticationResult ->
                        encryptAndStoreServerToken(authenticationResult, token)
                    }
                ) {
                    navigateToHomePage()
                }
            val promptInfo =
                BiometricPromptUtils.createPromptInfoForEncryption(requireActivity() as AppCompatActivity)
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        }
    }


    private fun decryptServerTokenFromStorage(authResult: BiometricPrompt.AuthenticationResult) {
        ciphertextWrapper?.let { textWrapper ->
            authResult.cryptoObject?.cipher?.let {

                var token = cryptographyManager.decryptData(textWrapper.ciphertext, it)
                loginViewModel.loginWithToken(token)
            }
        }

    }

    private fun encryptAndStoreServerToken(
        authResult: BiometricPrompt.AuthenticationResult,
        token: String
    ) {
        authResult.cryptoObject?.cipher?.apply {

            val encryptedServerTokenWrapper = cryptographyManager.encryptData(token, this)
            cryptographyManager.persistCiphertextWrapperToSharedPrefs(
                encryptedServerTokenWrapper,
                requireContext(),
                SHARED_PREFS_FILENAME,
                Context.MODE_PRIVATE,
                CIPHERTEXT_WRAPPER
            )
        }

        navigateToHomePage()

    }

    private fun navigateToHomePage() {
        activity?.let { activity ->

            val intent = Intent(activity, HomePageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            activity.finish();
        }

    }


    private lateinit var loginViewModel: LoginViewModel


    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName

        Toast.makeText(
            context,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(context, errorString, Toast.LENGTH_SHORT).show()
    }
}