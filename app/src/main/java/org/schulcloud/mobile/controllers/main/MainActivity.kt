package org.schulcloud.mobile.controllers.main

import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.google.android.material.bottomappbar.BottomAppBar
import kotlinx.android.synthetic.main.activity_main.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseActivity
import org.schulcloud.mobile.utils.visibilityBool
import org.schulcloud.mobile.viewmodels.MainViewModel

class MainActivity : BaseActivity() {
    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }

    private val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.config.observe(this, Observer { config ->
            bottomAppBar.menu.clear()
            if (config.menuRes != 0)
                bottomAppBar.inflateMenu(config.menuRes)
            bottomAppBar.inflateMenu(R.menu.fragment_main)
            if (!config.supportsRefresh)
                bottomAppBar.menu.findItem(R.id.base_action_refresh)?.isVisible = false

            fab.visibilityBool = config.fabVisible && config.fabIconRes != 0
            bottomAppBar.fabAlignmentMode = when (config.fragmentType) {
                FragmentType.PRIMARY -> BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
                FragmentType.SECONDARY -> BottomAppBar.FAB_ALIGNMENT_MODE_END
            }
            fab.setImageResource(config.fabIconRes)
        })

        bottomAppBar.setNavigationOnClickListener {
            val navDrawer = org.schulcloud.mobile.controllers.main.NavigationDrawerFragment()
            navDrawer.show(supportFragmentManager, navDrawer.tag)
        }
        bottomAppBar.setOnMenuItemClickListener {
            viewModel.onOptionsItemSelected.value = it
            return@setOnMenuItemClickListener true
        }

        fab.setOnClickListener { viewModel.onFabClicked.call() }
    }

    override fun onSupportNavigateUp() = findNavController(navHost).navigateUp()

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        viewModel.onOptionsItemSelected.value = item
        return true
    }
}
