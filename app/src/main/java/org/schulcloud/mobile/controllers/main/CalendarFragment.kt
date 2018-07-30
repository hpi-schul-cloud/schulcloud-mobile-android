package org.schulcloud.mobile.controllers.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.*
import com.jonaswanke.calendar.Week
import kotlinx.android.synthetic.main.fragment_calendar.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseFragment
import org.schulcloud.mobile.models.event.EventRepository
import org.schulcloud.mobile.utils.HOST
import org.schulcloud.mobile.viewmodels.CalendarViewModel

class CalendarFragment : BaseFragment() {
    companion object {
        val TAG: String = CalendarFragment::class.java.simpleName
    }

    override var url: String? = "$HOST/calendar"

    private lateinit var viewModel: CalendarViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(this).get(CalendarViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.title = getString(R.string.calendar_title)
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout = swipeRefresh

        calendar.apply {
            eventRequestCallback = { fetchEventsForWeek(it) }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.fragment_base, menu)
        inflater?.inflate(R.menu.fragment_calendar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.calendar_action_jumpToToday -> calendar.currentWeek = Week()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override suspend fun refresh() {
        EventRepository.syncEvents()
    }

    private fun fetchEventsForWeek(week: Week) {
        EventRepository.eventsForWeek(viewModel.realm, week).observe(this, Observer { events ->
            launch(UI) {
                val mapped = events?.map {
                    val title = it.title ?: return@map null
                    val location = it.location ?: return@map null
                    val start = it.start ?: return@map null
                    val end = it.end ?: return@map null
                    com.jonaswanke.calendar.Event(title, location, null, start, end)
                }
                        ?.filterNotNull()
                        ?.toList() ?: emptyList()
                calendar.setEventsForWeek(week, mapped)
            }
        })
    }
}
