package org.schulcloud.mobile.controllers.dashboard

import android.databinding.ViewDataBinding
import android.text.format.DateUtils
import org.schulcloud.mobile.controllers.base.BaseViewHolder
import org.schulcloud.mobile.controllers.base.OnItemSelectedCallback
import org.schulcloud.mobile.databinding.ItemEventBinding
import org.schulcloud.mobile.databinding.ItemEventCurrentBinding
import org.schulcloud.mobile.models.event.Event
import org.schulcloud.mobile.utils.getUserCalendar
import org.schulcloud.mobile.utils.timeOfDay
import org.schulcloud.mobile.utils.toLocal
import java.text.DateFormat


sealed class EventViewHolder<B : ViewDataBinding>(binding: B) : BaseViewHolder<Event, B>(binding) {
    val formattedTime: String
        get() = item.nextStart(true)?.let {
            DateUtils.formatSameDayTime(it.apply { toLocal() }.timeInMillis, getUserCalendar().timeInMillis,
                    DateFormat.SHORT, DateFormat.SHORT).toString()
        } ?: ""
}

class GeneralEventViewHolder(binding: ItemEventBinding, private val courseEventSelectedCallback: OnItemSelectedCallback<String>) : EventViewHolder<ItemEventBinding>(binding) {
    override fun onItemSet() {
        binding.event = item
        binding.formattedTime = formattedTime

        item.courseId?.also { courseId ->
            binding.container.setOnClickListener {
                courseEventSelectedCallback.onItemSelected(courseId)
            }
        } ?: binding.container.setOnClickListener(null)
    }
}

class CurrentEventViewHolder(binding: ItemEventCurrentBinding, private val courseEventSelectedCallback: OnItemSelectedCallback<String>) : EventViewHolder<ItemEventCurrentBinding>(binding) {
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

    override fun onItemSet() {
        binding.event = item
        binding.formattedTime = formattedTime

        item.courseId?.also { courseId ->
            binding.container.setOnClickListener {
                courseEventSelectedCallback.onItemSelected(courseId)
            }
        } ?: binding.container.setOnClickListener(null)
    }
}