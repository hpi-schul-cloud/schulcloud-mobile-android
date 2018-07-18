package org.schulcloud.mobile.controllers.dashboard

import android.databinding.ViewDataBinding
import android.view.ViewGroup
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseAdapter
import org.schulcloud.mobile.controllers.base.BaseViewHolder.Companion.createBinding
import org.schulcloud.mobile.controllers.base.OnItemSelectedCallback
import org.schulcloud.mobile.models.event.Event
import org.schulcloud.mobile.utils.getUserCalendar
import org.schulcloud.mobile.utils.timeOfDay
import org.schulcloud.mobile.utils.toLocal

class EventAdapter(private val courseEventSelectedCallback: OnItemSelectedCallback<String>)
    : BaseAdapter<Event, EventViewHolder<out ViewDataBinding>, ViewDataBinding>() {
    companion object {
        const val HOLDER_GENERAL = 0
        const val HOLDER_CURRENT = 1
    }


    fun update(events: List<Event>) {
        items = events
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder<out ViewDataBinding> {
        return when (viewType) {
            HOLDER_CURRENT -> CurrentEventViewHolder(createBinding(parent, R.layout.item_event_current),
                    courseEventSelectedCallback)
            else -> GeneralEventViewHolder(createBinding(parent, R.layout.item_event),
                    courseEventSelectedCallback)
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
}
