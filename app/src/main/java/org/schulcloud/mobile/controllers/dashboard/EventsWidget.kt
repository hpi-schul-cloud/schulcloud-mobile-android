package org.schulcloud.mobile.controllers.dashboard

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.widget_events.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.course.CourseActivity
import org.schulcloud.mobile.models.event.EventRepository
import org.schulcloud.mobile.viewmodels.EventListViewModel
import org.schulcloud.mobile.views.NoScrollLinearLayoutManager

class EventsWidget : Widget() {
    companion object {
        val TAG: String = EventsWidget::class.java.simpleName
    }

    private lateinit var viewModel: EventListViewModel
    private val eventAdapter: EventAdapter by lazy {
        EventAdapter {
            startActivity(CourseActivity.newIntent(context!!, it))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(EventListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.widget_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.events.observe(this, Observer { events ->
            eventAdapter.update(events ?: emptyList())
        })

        recyclerView.apply {
            layoutManager = NoScrollLinearLayoutManager(context)
            adapter = eventAdapter
        }
        eventAdapter.emptyIndicator = empty
    }

    override suspend fun refresh() {
        EventRepository.syncEvents()
    }
}
