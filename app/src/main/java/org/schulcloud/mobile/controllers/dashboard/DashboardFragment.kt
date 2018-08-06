package org.schulcloud.mobile.controllers.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.main.FragmentType
import org.schulcloud.mobile.controllers.main.MainFragment
import org.schulcloud.mobile.controllers.main.MainFragmentConfig

class DashboardFragment : MainFragment() {
    companion object {
        val TAG: String = DashboardFragment::class.java.simpleName
    }

    override val config: MainFragmentConfig = MainFragmentConfig(
            fragmentType = FragmentType.PRIMARY,
            fabIconRes = R.drawable.ic_share_white_24dp
    )
    override val title get() = getString(R.string.dashboard_title)
    override var url: String? = "/dashboard"


    private var widgets: Array<Widget> = emptyArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

    override suspend fun refresh() {
        for (widget in widgets)
            widget.refresh()
    }

    override fun onFabClicked() {
        if (context != null) {
            Toast.makeText(context, "FAB clicked", Toast.LENGTH_SHORT).show()
        }
    }


    private fun provideWidgets(): Array<Widget> {
        return arrayOf(
                org.schulcloud.mobile.controllers.dashboard.EventsWidget(),
                org.schulcloud.mobile.controllers.dashboard.HomeworkWidget(),
                NewsWidget()
        )
    }
}
