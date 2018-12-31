package org.schulcloud.mobile.controllers.base

import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import org.schulcloud.mobile.R
import org.schulcloud.mobile.utils.*
import java.util.*
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.suspendCoroutine
import kotlin.properties.Delegates


abstract class BaseActivity : AppCompatActivity() {
    companion object {
        const val KEY_RECREATE_FLAG_UI_MODE_CHANGE = "KEY_RECREATE_FLAG_UI_MODE_CHANGE"
    }

    open var url: String? = null
    var swipeRefreshLayout by Delegates.observable<SwipeRefreshLayout?>(null) { _, _, new ->
        new?.setup()
        new?.setOnRefreshListener { performRefresh() }
    }

    private val permissionRequests: MutableList<Continuation<Boolean>>
            by lazy { LinkedList<Continuation<Boolean>>() }

    private var lastThemeConfig: ThemeConfig? = null
    private var recreateFlagUiModeChange = false
    private val themeConfigChangeListener: ThemeConfigChangeListener = { _ ->
        recreateIfNecessary()
    }

    // region Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(ThemeConfigUtils.getInstance(this).themeConfig.themeOverlay)
        super.onCreate(savedInstanceState)

        if (savedInstanceState?.getBoolean(KEY_RECREATE_FLAG_UI_MODE_CHANGE) == true)
            flushResourceCache()
    }

    override fun onResume() {
        super.onResume()

        recreateIfNecessary()
        ThemeConfigUtils.getInstance(this).addThemeChangeListener(themeConfigChangeListener)
    }

    override fun onPause() {
        super.onPause()

        ThemeConfigUtils.getInstance(this).removeThemeChangeListener(themeConfigChangeListener)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(KEY_RECREATE_FLAG_UI_MODE_CHANGE, recreateFlagUiModeChange)
    }
    // endregion

    // region Activity
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.base_action_share -> shareLink(url!!, supportActionBar?.title)
            R.id.base_action_refresh -> performRefresh()
        // TODO: Remove when deep linking is readded
            R.id.base_action_openInBrowser -> openUrl(url.asUri())
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        // Request not from this class
        if (requestCode >= permissionRequests.size) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        //The request was interrupted
        if (permissions.isEmpty()) {
            permissionRequests[requestCode].resume(false)
            return
        }

        permissionRequests[requestCode].resume(grantResults[0] == PackageManager.PERMISSION_GRANTED)
    }
    // endregion

    protected fun setupActionBar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    protected open suspend fun refresh() {}
    protected fun performRefresh() {
        swipeRefreshLayout?.isRefreshing = true
        launch {
            withContext(UI) { refresh() }
            withContext(UI) { swipeRefreshLayout?.isRefreshing = false }
        }
    }

    suspend fun requestPermission(permission: String): Boolean = suspendCoroutine { cont ->
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            cont.resume(true)
            return@suspendCoroutine
        }

        permissionRequests.add(cont)
        ActivityCompat.requestPermissions(this, arrayOf(permission), permissionRequests.size - 1)
    }


    // region Helpers
    private fun recreateIfNecessary() {
        val themeConfig = ThemeConfigUtils.getInstance(this).themeConfig
        if (lastThemeConfig != null && lastThemeConfig != themeConfig) {
            if (lastThemeConfig?.darkMode != themeConfig.darkMode)
                recreateFlagUiModeChange = true
            recreate()
        }
        lastThemeConfig = themeConfig
    }

    /**
     * The resource cache isn't properly invalidated after a uiMode configuration change, hence we have to do it manually.
     * Source: [https://issuetracker.google.com/issues/37110398]
     */
    private fun flushResourceCache() {
        val oldConfig = resources.configuration
        val displayMetrics = resources.displayMetrics
        val originalFontScale = oldConfig.fontScale
        val newConfig = Configuration(oldConfig)
        newConfig.fontScale += 1f // fake value to force cache flush
        resources.updateConfiguration(newConfig, displayMetrics)
        newConfig.fontScale = originalFontScale
        resources.updateConfiguration(newConfig, displayMetrics)
    }
    // endregion
}
