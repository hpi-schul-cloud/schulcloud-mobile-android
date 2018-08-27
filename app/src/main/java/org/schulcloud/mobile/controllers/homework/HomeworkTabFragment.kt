package org.schulcloud.mobile.controllers.homework

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseFragment
import org.schulcloud.mobile.controllers.main.Refreshable
import org.schulcloud.mobile.controllers.main.RefreshableImpl
import org.schulcloud.mobile.viewmodels.HomeworkViewModel


@SuppressLint("ValidFragment")
abstract class HomeworkTabFragment(private val refreshableImpl: RefreshableImpl = RefreshableImpl()) : BaseFragment(),
        Refreshable by refreshableImpl {
    protected lateinit var viewModel: HomeworkViewModel

    init {
        refreshableImpl.refresh = { refresh() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = (parentFragment as HomeworkFragment).viewModel
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        performRefresh()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        refreshableImpl.swipeRefreshLayout = view.findViewById(R.id.swipeRefresh)
    }

    abstract suspend fun refresh()
}
