package org.schulcloud.mobile.controllers.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseFragment


@SuppressLint("ValidFragment")
abstract class TabFragment<P : MainFragment<VM>, VM : ViewModel>(private val refreshableImpl: RefreshableImpl = RefreshableImpl()) :
        BaseFragment(),
        Refreshable by refreshableImpl {
    protected lateinit var viewModel: VM

    init {
        refreshableImpl.refresh = { refresh() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        @Suppress("UNCHECKED_CAST")
        viewModel = (parentFragment as P).viewModel
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
