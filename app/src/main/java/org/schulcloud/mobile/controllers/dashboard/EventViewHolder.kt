package org.schulcloud.mobile.controllers.dashboard

import android.text.format.DateUtils
import androidx.databinding.ViewDataBinding
import org.schulcloud.mobile.controllers.base.BaseViewHolder
import org.schulcloud.mobile.databinding.ItemEventBinding
import org.schulcloud.mobile.databinding.ItemEventCurrentBinding
import org.schulcloud.mobile.models.event.Event
import org.schulcloud.mobile.utils.getUserCalendar
import org.schulcloud.mobile.utils.timeOfDay
import org.schulcloud.mobile.utils.toLocal
import org.schulcloud.mobile.utils.visibilityBool
import java.text.DateFormat


sealed class EventViewHolder<out B : ViewDataBinding>(binding: B) : BaseViewHolder<Event, B>(binding) {
    val formattedTime: String
        get() = item.nextStart(true)?.let {
            DateUtils.formatSameDayTime(it.apply { toLocal() }.timeInMillis, getUserCalendar().timeInMillis,
                    DateFormat.SHORT, DateFormat.SHORT).toString()
        } ?: ""
}

class GeneralEventViewHolder(binding: ItemEventBinding, private val onCourseEventSelected: (String) -> Unit)
    : EventViewHolder<ItemEventBinding>(binding) {
    override fun onItemSet() {
        binding.event = item
        binding.formattedTime = formattedTime

        item.courseId?.also { courseId ->
            binding.container.setOnClickListener {
                onCourseEventSelected(courseId)
            }
        } ?: binding.container.setOnClickListener(null)
    }
}

class CurrentEventViewHolder(binding: ItemEventCurrentBinding, private val onCourseEventSelected: (String) -> Unit)
    : EventViewHolder<ItemEventCurrentBinding>(binding) {
    companion object {
        @JvmStatic
        fun getProgress(event: Event): Int {
            val cal = getUserCalendar()
            val now = cal.timeOfDay

            val start = cal.apply {
                timeInMillis = event.start ?: return 0
                toLocal()
            }.timeOfDay
            val end = cal.apply {
                timeInMillis = event.end ?: return 0
                toLocal()
            }.timeOfDay

            return (100 * (now - start) / (end - start)).toInt()
        }
    }

    var items: List<Event> = emptyList()

    override fun onItemSet() {
        binding.event = item
        binding.formattedTime = formattedTime

        item.courseId?.also { courseId ->
            binding.container.setOnClickListener {
                onCourseEventSelected(courseId)
            }
        } ?: binding.container.setOnClickListener(null)

        binding.next.visibilityBool = items.size > 1
    }
}
