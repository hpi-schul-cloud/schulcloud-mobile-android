package org.schulcloud.mobile.controllers.course

import android.view.LayoutInflater
import android.view.ViewGroup
import org.schulcloud.mobile.controllers.base.BaseAdapter
import org.schulcloud.mobile.controllers.base.BaseViewHolder
import org.schulcloud.mobile.controllers.base.OnItemSelectedCallback
import org.schulcloud.mobile.databinding.ItemTopicBinding
import org.schulcloud.mobile.models.topic.Topic

class TopicListAdapter(private val selectedCallback: OnItemSelectedCallback)
    : BaseAdapter<Topic, TopicListAdapter.TopicViewHolder, ItemTopicBinding>() {

    fun update(topicList: List<Topic>) {
        items = topicList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val binding = ItemTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.selectedCallback = selectedCallback
        return TopicViewHolder(binding)
    }

    class TopicViewHolder(binding: ItemTopicBinding) : BaseViewHolder<Topic, ItemTopicBinding>(binding) {
        override fun onItemSet() {
            binding.topic = item
        }
    }
}
