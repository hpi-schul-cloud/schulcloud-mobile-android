package org.schulcloud.mobile.controllers.main

import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import org.schulcloud.mobile.controllers.login.LoginActivity
import org.schulcloud.mobile.models.user.UserRepository

class SplashActivity : AppCompatActivity() {

    companion object {
        val TAG: String = SplashActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(UserRepository.isAuthorized) {
            startApp()
        } else {
            startLoginActivity()
        }
    }

    private fun startLoginActivity() {
        val intent = Intent(this@SplashActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startApp() {
        val intent = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}
