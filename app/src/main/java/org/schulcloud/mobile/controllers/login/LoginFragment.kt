package org.schulcloud.mobile.controllers.login

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_login.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseFragment
import org.schulcloud.mobile.controllers.main.MainActivity
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.viewmodels.LoginViewModel

class LoginFragment: BaseFragment() {

    companion object {
        val TAG: String = LoginFragment::class.java.simpleName
    }

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnLogin.setOnClickListener { login() }
        btn_demo_student.setOnClickListener { demoLoginStudent() }
        btn_demo_teacher.setOnClickListener { demoLoginTeacher() }
        handleLoginStatus()
    }

    private fun login() {
        val email = editEmail.text.toString().trim()
        val password = editPassword.text.toString()
        loginViewModel.login(email, password)
    }

    private fun demoLoginStudent() {
        loginViewModel.login("demo-schueler@schul-cloud.org", "schulcloud")
    }

    private fun demoLoginTeacher() {
        loginViewModel.login("demo-lehrer@schul-cloud.org", "schulcloud")
    }

    private fun handleLoginStatus() {
        loginViewModel.loginState.observe(this, Observer { loginState ->
            when(loginState) {
                is LoginViewModel.LoginStatus.Pending -> {
                    Log.d(TAG, "PENDING LOGIN")
                    login_progress.visibility = View.VISIBLE
                    error_text.visibility = View.GONE
                }
                is LoginViewModel.LoginStatus.LoggedIn -> {
                    Log.d(TAG, "LOGGED IN")
                    startMainActivity()
                    error_text.visibility = View.GONE
                }
                is LoginViewModel.LoginStatus.InvalidInputs -> {
                    Log.d(TAG, "INVALID FIELDS")
                    login_progress.visibility = View.GONE
                    handleInvalidFields(loginState.invalidInputs)
                    error_text.visibility = View.GONE
                }
                is LoginViewModel.LoginStatus.Error -> {
                    Log.d(TAG, "ERROR: " + loginState.error)
                    login_progress.visibility = View.GONE
                    error_text.visibility = View.VISIBLE
                    if(loginState.error == RequestJobCallback.ErrorCode.TIMEOUT.toString()){
                        error_text.text = getString(R.string.login_error_server)
                    }else if(loginState.error == RequestJobCallback.ErrorCode.ERROR.toString())
                    {
                        error_text.text = getString(R.string.login_error_passwordWrong)
                    }else if(loginState.error == RequestJobCallback.ErrorCode.NO_NETWORK.toString()){
                        error_text.text = getString(R.string.login_error_noConnection)
                    }

                }
            }
        })
    }

    private fun handleInvalidFields(invalidInputs: MutableList<LoginViewModel.LoginInput>) {
        invalidInputs.forEach { input ->
            when(input) {
                LoginViewModel.LoginInput.EMAIL -> editEmail.error = getString(R.string.login_error_emailInvalid)
                LoginViewModel.LoginInput.PASSWORD -> editPassword.error = getString(R.string.login_error_passwordEmpty)
            }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}