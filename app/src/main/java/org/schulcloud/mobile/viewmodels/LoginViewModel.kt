package org.schulcloud.mobile.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.async
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.user.UserRepository

class LoginViewModel : ViewModel() {

    val loginState = MutableLiveData<LoginStatus>()

    fun login(email: String, password: String) {

        val invalidInputs = mutableListOf<LoginInput>()
        if (!isEmailValid(email)) {
            invalidInputs.add(LoginInput.EMAIL)
        }
        if (password.isEmpty()) {
            invalidInputs.add(LoginInput.PASSWORD)
        }

        if (invalidInputs.isEmpty()) {
            loginState.value = LoginStatus.Pending()
            async {
                UserRepository.login(email, password, loginCallback())
            }
        } else {
            loginState.value = LoginStatus.InvalidInputs(invalidInputs)
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun loginCallback(): RequestJobCallback {
        return object : RequestJobCallback() {
            override fun onSuccess() {
                loginState.value = LoginStatus.LoggedIn()
            }
            override fun onError(code: ErrorCode) {
                loginState.value = LoginStatus.Error(code.toString())
            }
        }
    }

    sealed class LoginStatus {
        class Pending : LoginStatus()
        class LoggedIn : LoginStatus()
        class InvalidInputs(val invalidInputs: MutableList<LoginInput>): LoginStatus()
        class Error(val error: String) : LoginStatus()
    }

    enum class LoginInput { EMAIL, PASSWORD }
}