package org.schulcloud.mobile.controllers.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.*
import com.alamkanak.weekview.DateTimeInterpreter
import com.alamkanak.weekview.MonthLoader
import com.alamkanak.weekview.WeekViewEvent
import kotlinx.android.synthetic.main.fragment_calendar.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseFragment
import org.schulcloud.mobile.models.event.EventRepository
import org.schulcloud.mobile.utils.HOST
import org.schulcloud.mobile.utils.asUserCalendar
import org.schulcloud.mobile.viewmodels.CalendarViewModel
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : BaseFragment() {
    companion object {
        val TAG: String = CalendarFragment::class.java.simpleName

        private val FORMAT_DATE: java.text.DateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT)
        private val FORMAT_TIME: java.text.DateFormat = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)
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

        weekView.apply {
            dateTimeInterpreter = object : DateTimeInterpreter {
                override fun interpretDate(date: Calendar?): String {
                    if (date == null)
                        return ""
                    return FORMAT_DATE.format(date.time)
                }

                override fun interpretTime(hour: Int, minutes: Int): String {
                    return hour.toString()
                }
            }
            monthChangeListener = MonthLoader.MonthChangeListener { year, month ->
                // TODO
                val events = viewModel.events.value
                val ret = if (events?.containsKey(year to month - 1) == true)
                    events[year to month - 1]?.toList()?.map {
                        val start = it.start ?: return@map null
                        val end = it.end ?: return@map null
                        WeekViewEvent(it.id, it.title, it.location, start.asUserCalendar(), end.asUserCalendar())
                    } ?: emptyList()
                else {
                    fetchEventsForMonth(year, month - 1)
                    emptyList<WeekViewEvent>()
                }

                ret
            }
        }

//        viewModel.eventsForMonth(2018, 7).observe(this, Observer {
//            weekView.notifyDatasetChanged()
//            it?.apply {
//                Log.i(TAG, it.joinToString(", ") ?: "")
//            } ?: Log.i(TAG, "eventsForMonth null")
//        })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.fragment_base, menu)
        inflater?.inflate(R.menu.fragment_calendar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override suspend fun refresh() {
        EventRepository.syncEvents()
    }

    private fun fetchEventsForMonth(year: Int, month: Int) {
        EventRepository.eventsForMonth(viewModel.realm, year, month).observe(this, Observer { events ->
            viewModel.events.value = (viewModel.events.value ?: mutableMapOf()).apply {
                events?.let {
                    this[year to month] = it
                }
            }
            weekView.notifyDatasetChanged()
        })
    }
}
