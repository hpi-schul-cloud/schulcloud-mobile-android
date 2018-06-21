package org.schulcloud.mobile.controllers.base

import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.schulcloud.mobile.R
import org.schulcloud.mobile.utils.setup
import kotlin.properties.Delegates

abstract class BaseActivity : AppCompatActivity() {
    var swipeRefreshLayout by Delegates.observable<SwipeRefreshLayout?>(null) { _, _, new ->
        new?.setup()
        new?.setOnRefreshListener { performRefresh() }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.refresh -> performRefresh()
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
    private fun performRefresh() {
        swipeRefreshLayout?.isRefreshing = true
        async(UI) { refresh() }
        swipeRefreshLayout?.isRefreshing = false
    }
}
