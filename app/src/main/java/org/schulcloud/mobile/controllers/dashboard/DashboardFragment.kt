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


    private var widgets: Array<Widget> = emptyArray()

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

        if (childFragmentManager.fragments.isEmpty()) {
            widgets = provideWidgets()
            childFragmentManager.beginTransaction().apply {
                for (widget in widgets)
                    add(R.id.contentList, widget, widget.javaClass.simpleName)
            }.commit()
        } else
            widgets = childFragmentManager.fragments
                    .map { it as Widget }.toTypedArray()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.fragment_base, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override suspend fun refresh() {
        for (widget in widgets)
            widget.refresh()
    }

    private fun provideWidgets(): Array<Widget> {
        return arrayOf(
                EventsWidget(),
                HomeworkWidget(),
                NewsWidget()
        )
    }
}
