package org.schulcloud.mobile.controllers.dashboard

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import org.schulcloud.mobile.controllers.base.BaseAdapter
import org.schulcloud.mobile.controllers.base.BaseViewHolder
import org.schulcloud.mobile.controllers.base.OnItemSelectedCallback
import org.schulcloud.mobile.databinding.ItemEventBinding
import org.schulcloud.mobile.models.event.Event
import org.schulcloud.mobile.utils.getUserCalendar
import java.text.DateFormat

class EventAdapter(private val courseEventSelectedCallback: OnItemSelectedCallback<String>)
    : BaseAdapter<Event, EventAdapter.EventViewHolder, ItemEventBinding>() {

    fun update(events: List<Event>) {
        items = events
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventAdapter.EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    inner class EventViewHolder(binding: ItemEventBinding) : BaseViewHolder<Event, ItemEventBinding>(binding) {
        override fun onItemSet() {
            binding.event = item

            item.courseId?.also { courseId ->
                binding.container.setOnClickListener {
                    courseEventSelectedCallback.onItemSelected(courseId)
                }
            } ?: binding.container.setOnClickListener(null)

            val cal = getUserCalendar()
            binding.formattedTime = item.nextStart(true)?.let {
                DateUtils.formatSameDayTime(it.timeInMillis, cal.timeInMillis,
                        DateFormat.SHORT, DateFormat.SHORT).toString()
            } ?: ""
        }
    }
}
