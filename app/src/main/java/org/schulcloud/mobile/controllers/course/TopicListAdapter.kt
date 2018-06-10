package org.schulcloud.mobile.controllers.course

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import org.schulcloud.mobile.databinding.ItemTopicBinding
import org.schulcloud.mobile.models.topic.Topic

/**
 * Date: 6/10/2018
 */
class TopicListAdapter(private val selectedCallback: OnTopicSelectedCallback) : RecyclerView.Adapter<TopicListAdapter.TopicViewHolder>() {
    private var topics: List<Topic> = emptyList()

    fun update(topicList: List<Topic>) {
        topics = topicList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val binding = ItemTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.selectedCallback = selectedCallback
        return TopicViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return topics.size
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        holder.binding.topic = topics[position]
    }

    class TopicViewHolder(val binding: ItemTopicBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnTopicSelectedCallback {
        fun onTopicSelected(id: String)
    }
}