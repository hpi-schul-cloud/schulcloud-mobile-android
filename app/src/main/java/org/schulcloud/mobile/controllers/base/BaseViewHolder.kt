package org.schulcloud.mobile.controllers.base

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

/**
 * Date: 6/11/2018
 */
abstract class BaseViewHolder<B : ViewDataBinding>(val binding: B) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun <B : ViewDataBinding> createBinding(parent: ViewGroup, @LayoutRes layoutId: Int): B {
            return DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutId,
                    parent, false)
        }
    }
}
