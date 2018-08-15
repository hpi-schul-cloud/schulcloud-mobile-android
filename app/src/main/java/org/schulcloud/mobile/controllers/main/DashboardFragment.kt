package org.schulcloud.mobile.controllers.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseFragment

class DashboardFragment : BaseFragment() {
    companion object {
        val TAG: String = DashboardFragment::class.java.simpleName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.title = getString(R.string.dashboard_title)
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }
}
