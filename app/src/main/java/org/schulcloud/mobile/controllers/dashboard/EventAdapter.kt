package org.schulcloud.mobile.controllers.dashboard

import androidx.databinding.ViewDataBinding
import android.view.ViewGroup
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseAdapter
import org.schulcloud.mobile.controllers.base.BaseViewHolder.Companion.createBinding
import org.schulcloud.mobile.models.event.Event
import org.schulcloud.mobile.utils.getUserCalendar
import org.schulcloud.mobile.utils.timeOfDay
import org.schulcloud.mobile.utils.toLocal

class EventAdapter(private val onCourseEventSelected: (String) -> Unit)
    : BaseAdapter<Event, EventViewHolder<ViewDataBinding>, ViewDataBinding>() {
    companion object {
        const val HOLDER_GENERAL = 0
        const val HOLDER_CURRENT = 1
    }


    fun update(events: List<Event>) {
        items = events
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder<ViewDataBinding> {
        return when (viewType) {
            HOLDER_CURRENT -> CurrentEventViewHolder(createBinding(parent, R.layout.item_event_current),
                    onCourseEventSelected)
            else -> GeneralEventViewHolder(createBinding(parent, R.layout.item_event),
                    onCourseEventSelected)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val event = items[position]

        fun isEventCurrent(): Boolean {
            val cal = getUserCalendar()
            val currentTime = cal.timeOfDay
            val start = event.start ?: return false
            val startTime = cal.apply {
                timeInMillis = start
                toLocal()
            }.timeOfDay
            return startTime < currentTime
        }

        if (isEventCurrent())
            return HOLDER_CURRENT
        return HOLDER_GENERAL
    }

    override fun onBindViewHolder(holder: EventViewHolder<ViewDataBinding>, position: Int) {
        super.onBindViewHolder(holder, position)

        if (holder is CurrentEventViewHolder)
            holder.items = items
    }
}
