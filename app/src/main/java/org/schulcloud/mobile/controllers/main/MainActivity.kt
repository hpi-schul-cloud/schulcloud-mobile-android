package org.schulcloud.mobile.controllers.main

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseActivity
import org.schulcloud.mobile.controllers.dashboard.DashboardFragment
import org.schulcloud.mobile.controllers.login.LoginActivity
import org.schulcloud.mobile.models.user.UserRepository

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupNavigation(savedInstanceState == null)
    }

    private fun setupNavigation(isFirstRun: Boolean) {
        setSupportActionBar(bottomAppBar)
        //        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar,
        //                R.string.main_drawer_open, R.string.main_drawer_close)
        //        drawer_layout.addDrawerListener(toggle)
        //        toggle.syncState()
        //
        //        nav_view.setNavigationItemSelectedListener(this)
        //        if (isFirstRun) {
        //            nav_view.setCheckedItem(R.id.nav_dashboard)
        //            addFragment(DashboardFragment(), DashboardFragment.TAG)
        //        }
    }

    override fun onBackPressed() {
        //        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
        //            drawer_layout.closeDrawer(GravityCompat.START)
        //        } else {
        //            super.onBackPressed()
        //        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_dashboard ->
                replaceFragment(DashboardFragment(), DashboardFragment.TAG)
            R.id.nav_news ->
                replaceFragment(NewsListFragment(), NewsListFragment.TAG)
            R.id.nav_courses ->
                replaceFragment(CourseListFragment(), CourseListFragment.TAG)
            R.id.nav_assignments ->
                replaceFragment(HomeworkListFragment(), HomeworkListFragment.TAG)
            R.id.nav_files ->
                replaceFragment(FileOverviewFragment(), FileOverviewFragment.TAG)
            R.id.nav_logout -> {
                UserRepository.logout()
                showLoginActivity()
            }
        }

        //        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun addFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
                .add(R.id.container, fragment, tag)
                .commit()
    }

    fun replaceFragment(fragment: Fragment, tag: String) {
        //        supportFragmentManager.beginTransaction()
        //                .replace(R.id.container, fragment, tag)
        //                .addToBackStack(tag)
        //                .commit()
    }

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
