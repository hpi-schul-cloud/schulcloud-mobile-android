package org.schulcloud.mobile.controllers.main

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.MenuRes
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseFragment
import org.schulcloud.mobile.utils.HOST
import org.schulcloud.mobile.utils.combinePath
import org.schulcloud.mobile.utils.setup
import org.schulcloud.mobile.utils.shareLink
import org.schulcloud.mobile.viewmodels.MainViewModel
import java.io.File
import kotlin.properties.Delegates

abstract class MainFragment : BaseFragment() {
    abstract val config: MainFragmentConfig

    abstract val title: String
    open var url: String? = null
    protected var swipeRefreshLayout by Delegates.observable<SwipeRefreshLayout?>(null) { _, _, new ->
        new?.setup()
        new?.setOnRefreshListener { performRefresh() }
        new?.isRefreshing = isRefreshing
    }
    protected var isRefreshing: Boolean by Delegates.observable(false) { _, _, new ->
        swipeRefreshLayout?.isRefreshing = new
    }

    private val mainViewModel: MainViewModel by lazy {
        ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel.onFabClicked.observe(this, Observer { onFabClicked() })
        performRefresh()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh)
        (activity as MainActivity).setSupportActionBar(view.findViewById(R.id.toolbar))
        activity!!.title = title
    }

    override fun onResume() {
        super.onResume()
        notifyConfigChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.base_action_share -> {
                var link = url!!
                if (link.startsWith(File.pathSeparatorChar))
                    link = combinePath(HOST, link)
                context?.shareLink(link, activity?.title)
            }
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

    open fun onFabClicked() {}
    protected fun notifyConfigChanged() {
        mainViewModel.config.value = config
    }
}

data class MainFragmentConfig(
    val fragmentType: FragmentType = FragmentType.SECONDARY,

    @MenuRes
    val menuRes: Int = 0,
    val supportsRefresh: Boolean = true,

    val fabVisible: Boolean = true,
    @DrawableRes
    val fabIconRes: Int = 0
)

enum class FragmentType {
    PRIMARY,
    SECONDARY
}
