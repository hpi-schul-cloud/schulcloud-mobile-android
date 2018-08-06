package org.schulcloud.mobile.controllers.main

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.navigation.fragment.NavHostFragment.findNavController
import kotlinx.android.synthetic.main.activity_main.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseActivity
import org.schulcloud.mobile.controllers.login.LoginActivity

class MainActivity : BaseActivity() {

    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(bottomAppBar)
    }

    override fun onSupportNavigateUp() = findNavController(navHost).navigateUp()

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                val navDrawer = NavigationDrawerFragment()
                navDrawer.show(supportFragmentManager, navDrawer.tag)
            }

            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun showLoginActivity() {
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
