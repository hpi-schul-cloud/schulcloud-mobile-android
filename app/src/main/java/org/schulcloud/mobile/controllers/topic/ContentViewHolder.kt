package org.schulcloud.mobile.controllers.topic

import org.schulcloud.mobile.controllers.base.BaseViewHolder
import org.schulcloud.mobile.databinding.ItemContentBinding

/**
 * Date: 6/11/2018
 */
sealed class ContentViewHolder(binding: ItemContentBinding) : BaseViewHolder<ItemContentBinding>(binding) {
}

class TextViewHolder(binding: ItemContentBinding) : ContentViewHolder(binding)
