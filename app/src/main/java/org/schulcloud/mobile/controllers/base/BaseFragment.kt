package org.schulcloud.mobile.controllers.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.Menu
import android.view.MenuItem
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import org.schulcloud.mobile.R
import org.schulcloud.mobile.utils.setup
import org.schulcloud.mobile.utils.shareLink
import kotlin.properties.Delegates

abstract class BaseFragment : Fragment() {
    open var url: String? = null
    var swipeRefreshLayout by Delegates.observable<SwipeRefreshLayout?>(null) { _, _, new ->
        new?.setup()
        new?.setOnRefreshListener { performRefresh() }
        new?.isRefreshing = isRefreshing
    }
    var isRefreshing: Boolean by Delegates.observable(false) { _, _, new ->
        swipeRefreshLayout?.isRefreshing = new
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        performRefresh()
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        menu?.findItem(R.id.base_action_share)?.isVisible = url != null
        menu?.findItem(R.id.base_action_refresh)?.isVisible = swipeRefreshLayout != null
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.base_action_share -> context?.shareLink(url!!, activity?.title)
            R.id.base_action_refresh -> performRefresh()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    protected open suspend fun refresh() {}
    protected fun performRefresh() {
        isRefreshing = true
        launch {
            withContext(UI) { refresh() }
            withContext(UI) { isRefreshing = false }
        }
    }
}
