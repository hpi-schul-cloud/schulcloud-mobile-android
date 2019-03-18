package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.utils.emailIsValid

class LoginViewModel : ViewModel() {
    val loginState = MutableLiveData<LoginStatus>()

    fun login(email: String, password: String) {
        val invalidInputs = mutableListOf<LoginInput>()
        if (!emailIsValid(email))
            invalidInputs.add(LoginInput.EMAIL)
        if (password.isEmpty())
            invalidInputs.add(LoginInput.PASSWORD)
        loginState.value = if (invalidInputs.isNotEmpty())
            LoginStatus.InvalidInputs(invalidInputs)
        else {
            UserRepository.login(email, password, object : RequestJobCallback() {
                override fun onSuccess() {
                    loginState.value = LoginStatus.LoggedIn
                }

                override fun onError(code: ErrorCode) {
                    loginState.value = LoginStatus.Error(code)
                }
            })
            LoginStatus.Pending
        }
    }

    sealed class LoginStatus {
        object Pending : LoginStatus()
        object LoggedIn : LoginStatus()
        class InvalidInputs(val invalidInputs: MutableList<LoginInput>) : LoginStatus()
        class Error(val error: RequestJobCallback.ErrorCode) : LoginStatus()
    }

    enum class LoginInput { EMAIL, PASSWORD }
}
