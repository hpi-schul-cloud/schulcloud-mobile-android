package org.schulcloud.mobile.controllers.main

import android.graphics.Color.*
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.widget.ActionMenuView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.children
import androidx.core.view.doOnNextLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.ui.NavigationUI
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
    private val navController: NavController by lazy { findNavController(navHost) }
    private var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.config.observe(this, Observer { config ->
            supportActionBar?.title = config.title
            updateToolbarColor()

            bottomAppBar.apply {
                menu.clear()
                if (config.menuBottomRes != 0)
                    inflateMenu(config.menuBottomRes)
            }

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

    override fun setSupportActionBar(toolbar: Toolbar?) {
        super.setSupportActionBar(toolbar)

        this.toolbar = toolbar
        if (toolbar != null)
            NavigationUI.setupWithNavController(toolbar, navController)
        updateToolbarColor()
    }

    override fun onSupportNavigateUp() = navController.navigateUp()

    override fun onBackPressed() {
        if (!navController.popBackStack())
            super.onBackPressed()
    }

    override fun openOptionsMenu() {
        super.openOptionsMenu()
        updateToolbarColor()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        viewModel.onOptionsItemSelected.value = item
        return true
    }


    private fun updateToolbarColor() {
        val toolbar = toolbar ?: return
        val color = viewModel.config.value?.toolbarColor
                ?: ContextCompat.getColor(this, R.color.toolbar_background_default)

        // Formula from [Color#luminance()]
        val isLight = 0.2126 * red(color) + 0.7152 * green(color) + 0.0722 * blue(color) > 0.5 * 255

        val textColor = ContextCompat.getColor(this,
                if (isLight) R.color.material_text_primary_dark
                else R.color.material_text_primary_light)
        val textColorFilter = PorterDuffColorFilter(textColor, PorterDuff.Mode.SRC_ATOP)

        // Background
        toolbar.setBackgroundColor(color)

        // Icons
        for (view in toolbar.children)
            when (view) {
            // Back button
                is ImageButton -> {
                    view.drawable.colorFilter = textColorFilter
                }

            // Option items
                is ActionMenuView -> {
                    view.doOnNextLayout {
                        for (innerView in view.children) {
                            if (innerView !is ActionMenuItemView)
                                continue

                            for (drawable in innerView.compoundDrawables) {
                                if (drawable == null)
                                    continue

                                innerView.post {
                                    drawable.colorFilter = textColorFilter
                                }
                            }
                        }
                    }
                }
            }

        // Title + subtitle
        toolbar.setTitleTextColor(textColor)
        toolbar.setSubtitleTextColor(textColor)

        // Overflow icon
        toolbar.overflowIcon?.also {
            DrawableCompat.setTint(it, textColor)
        }
    }
}
