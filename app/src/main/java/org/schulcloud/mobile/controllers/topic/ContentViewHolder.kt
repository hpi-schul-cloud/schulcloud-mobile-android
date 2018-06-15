package org.schulcloud.mobile.controllers.topic

import android.databinding.ViewDataBinding
import android.net.Uri
import org.schulcloud.mobile.controllers.base.BaseViewHolder
import org.schulcloud.mobile.databinding.ItemContentEtherpadBinding
import org.schulcloud.mobile.databinding.ItemContentTextBinding
import org.schulcloud.mobile.databinding.ItemContentUnsupportedBinding
import org.schulcloud.mobile.models.content.ContentWrapper
import org.schulcloud.mobile.models.topic.Topic
import org.schulcloud.mobile.utils.HEADER_REFERER
import org.schulcloud.mobile.utils.openUrl

/**
 * Date: 6/11/2018
 */
sealed class ContentViewHolder<B : ViewDataBinding>(binding: B) : BaseViewHolder<B>(binding) {
    private lateinit var _content: ContentWrapper
    var content: ContentWrapper
        get() = _content
        set(value) {
            _content = value
            onContentSet()
        }

    lateinit var topic: Topic

    abstract fun onContentSet()
}

class TextViewHolder(binding: ItemContentTextBinding) : ContentViewHolder<ItemContentTextBinding>(binding) {
    override fun onContentSet() {
        binding.wrapper = content
        binding.content = content.content
    }
}

class EtherpadViewHolder(binding: ItemContentEtherpadBinding) : ContentViewHolder<ItemContentEtherpadBinding>(binding) {
    override fun onContentSet() {
        binding.wrapper = content
        binding.content = content.content
        binding.viewHolder = this

        binding.contentView.referer = "https://schul-cloud.org/courses/59a3c657a2049554a93fec3a/topics/5a7afee7994b406cfc028dd2/"
        binding.contentView.loadUrl(content.content?.url, mutableMapOf(HEADER_REFERER to "https://schul-cloud.org/courses/59a3c657a2049554a93fec3a/topics/5a7afee7994b406cfc028dd2/"))
    }

    fun openExternal() {
        openUrl(context, Uri.parse(content.content?.url), mapOf(HEADER_REFERER to topic.url))
    }
}

class UnsupportedViewHolder(binding: ItemContentUnsupportedBinding) : ContentViewHolder<ItemContentUnsupportedBinding>(binding) {
    override fun onContentSet() {
        binding.wrapper = content
        binding.content = content.content
    }
}
