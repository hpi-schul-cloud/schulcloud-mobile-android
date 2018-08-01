package org.schulcloud.mobile.controllers.login

import android.os.Bundle
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseActivity

class LoginActivity: BaseActivity() {

    companion object {
        val TAG: String = LoginActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if(supportFragmentManager.findFragmentByTag(LoginFragment.TAG) == null) {
            showLoginFragment()
        }
    }

    private fun showLoginFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.content, LoginFragment(), LoginFragment.TAG)
        transaction.commit()
    }
}
