package org.schulcloud.mobile.controllers.topic

import android.databinding.ViewDataBinding
import org.schulcloud.mobile.controllers.base.BaseViewHolder
import org.schulcloud.mobile.databinding.ItemContentTextBinding
import org.schulcloud.mobile.databinding.ItemContentUnsupportedBinding
import org.schulcloud.mobile.models.content.ContentWrapper

/**
 * Date: 6/11/2018
 */
sealed class ContentViewHolder<B : ViewDataBinding>(binding: B) : BaseViewHolder<B>(binding) {
    abstract fun setContent(content: ContentWrapper)
}

class TextViewHolder(binding: ItemContentTextBinding) : ContentViewHolder<ItemContentTextBinding>(binding) {
    override fun setContent(content: ContentWrapper) {
        binding.wrapper = content
        binding.content = content.content
    }
}

class UnsupportedViewHolder(binding: ItemContentUnsupportedBinding) : ContentViewHolder<ItemContentUnsupportedBinding>(binding) {
    override fun setContent(content: ContentWrapper) {
        binding.wrapper = content
        binding.content = content.content
    }
}
