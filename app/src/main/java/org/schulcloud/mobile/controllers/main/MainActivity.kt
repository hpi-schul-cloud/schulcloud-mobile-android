package org.schulcloud.mobile.controllers.main

import android.os.Bundle
import android.view.MenuItem
import androidx.navigation.fragment.NavHostFragment.findNavController
import kotlinx.android.synthetic.main.activity_main.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseActivity

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
}
