package org.schulcloud.mobile.controllers.dashboard

import android.os.Bundle
import android.view.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseFragment
import org.schulcloud.mobile.utils.HOST

class DashboardFragment : BaseFragment() {
    companion object {
        val TAG: String = DashboardFragment::class.java.simpleName
    }

    override var url: String? = "$HOST/dashboard"


    private val widgets = arrayOf(
            EventsWidget(),
            NewsWidget()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.title = getString(R.string.dashboard_title)
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout = swipeRefresh

        if (savedInstanceState == null)
            childFragmentManager.beginTransaction().apply {
                for (fragment in widgets)
                    add(R.id.contentList, fragment)
            }.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.fragment_base, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override suspend fun refresh() {
        for (widget in widgets)
            widget.refresh()
    }
}
