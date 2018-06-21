package org.schulcloud.mobile.controllers.base

import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.MenuItem
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.schulcloud.mobile.R
import org.schulcloud.mobile.utils.setup
import kotlin.properties.Delegates

abstract class BaseFragment : Fragment() {
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

    protected open suspend fun refresh() {}
    private fun performRefresh() {
        swipeRefreshLayout?.isRefreshing = true
        async(UI) { refresh() }
        swipeRefreshLayout?.isRefreshing = false
    }
}