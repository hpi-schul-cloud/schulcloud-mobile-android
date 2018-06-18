package org.schulcloud.mobile.controllers.topic

import android.databinding.ViewDataBinding
import android.support.v7.widget.LinearLayoutManager
import org.schulcloud.mobile.controllers.base.BaseViewHolder
import org.schulcloud.mobile.databinding.ItemContentEtherpadBinding
import org.schulcloud.mobile.databinding.ItemContentResourcesBinding
import org.schulcloud.mobile.databinding.ItemContentTextBinding
import org.schulcloud.mobile.databinding.ItemContentUnsupportedBinding
import org.schulcloud.mobile.models.content.ContentWrapper
import org.schulcloud.mobile.models.topic.Topic
import org.schulcloud.mobile.utils.HEADER_REFERER
import org.schulcloud.mobile.utils.asUri
import org.schulcloud.mobile.utils.openUrl

/**
 * Date: 6/11/2018
 */
sealed class ContentViewHolder<B : ViewDataBinding>(binding: B) : BaseViewHolder<ContentWrapper, B>(binding) {
    lateinit var topic: Topic
}

class TextViewHolder(binding: ItemContentTextBinding) : ContentViewHolder<ItemContentTextBinding>(binding) {
    override fun onItemSet() {
        binding.wrapper = item
        binding.content = item.content
    }
}

class ResourcesViewHolder(binding: ItemContentResourcesBinding) : ContentViewHolder<ItemContentResourcesBinding>(binding) {
    private val adapter: ResourceListAdapter

    init {
        adapter = ResourceListAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ResourcesViewHolder.adapter
        }
    }

    override fun onItemSet() {
        binding.wrapper = item
        binding.content = item.content

        adapter.update(item.content?.resources ?: emptyList())
    }

    fun openExternal() {
        openUrl(context, item.content?.url.asUri(), mapOf(HEADER_REFERER to topic.url))
    }
}

class EtherpadViewHolder(binding: ItemContentEtherpadBinding) : ContentViewHolder<ItemContentEtherpadBinding>(binding) {
    override fun onItemSet() {
        binding.wrapper = item
        binding.content = item.content
        binding.viewHolder = this

        binding.contentView.referer = "https://schul-cloud.org/courses/59a3c657a2049554a93fec3a/topics/5a7afee7994b406cfc028dd2/"
        binding.contentView.loadUrl(item.content?.url, mutableMapOf(HEADER_REFERER to "https://schul-cloud.org/courses/59a3c657a2049554a93fec3a/topics/5a7afee7994b406cfc028dd2/"))
    }

    fun openExternal() {
        openUrl(context, item.content?.url.asUri(), mapOf(HEADER_REFERER to topic.url))
    }
}

class UnsupportedViewHolder(binding: ItemContentUnsupportedBinding) : ContentViewHolder<ItemContentUnsupportedBinding>(binding) {
    override fun onItemSet() {
        binding.wrapper = item
        binding.content = item.content
    }
}
