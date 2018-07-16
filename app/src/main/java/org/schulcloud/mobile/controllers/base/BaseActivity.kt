package org.schulcloud.mobile.controllers.base

import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import org.schulcloud.mobile.R
import org.schulcloud.mobile.utils.asUri
import org.schulcloud.mobile.utils.openUrl
import org.schulcloud.mobile.utils.setup
import org.schulcloud.mobile.utils.shareLink
import kotlin.properties.Delegates

abstract class BaseActivity : AppCompatActivity() {
    open var url: String? = null
    var swipeRefreshLayout by Delegates.observable<SwipeRefreshLayout?>(null) { _, _, new ->
        new?.setup()
        new?.setOnRefreshListener { performRefresh() }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.activity_action_share -> shareLink(url!!, supportActionBar?.title)
        // TODO: Remove when deep linking is readded
            R.id.activity_action_openInBrowser -> openUrl(url.asUri())
            R.id.activity_action_refresh -> performRefresh()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    protected fun setupActionBar() {
        if (toolbar != null)
            setSupportActionBar(toolbar)

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
}
