package org.schulcloud.mobile.controllers.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import org.schulcloud.mobile.controllers.base.BaseAdapter
import org.schulcloud.mobile.controllers.base.BaseViewHolder
import org.schulcloud.mobile.controllers.base.OnItemSelectedCallback
import org.schulcloud.mobile.databinding.ItemEventBinding
import org.schulcloud.mobile.models.event.Event

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



        }
    }
}
