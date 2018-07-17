package org.schulcloud.mobile.controllers.base

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

abstract class BaseViewHolder<T : Any, out B : ViewDataBinding>(val binding: B) : RecyclerView.ViewHolder(binding.root), LifecycleOwner {
    companion object {
        fun <B : ViewDataBinding> createBinding(parent: ViewGroup, @LayoutRes layoutId: Int): B {
            return DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutId,
                    parent, false)
        }
    }

    val context: Context
        get() = binding.root.context
    private val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

    private lateinit var _item: T
    var item: T
        get() = _item
        set(value) {
            _item = value
            lifecycleRegistry.markState(Lifecycle.State.CREATED)
            lifecycleRegistry.markState(Lifecycle.State.STARTED)
            onItemSet()
        }

    init {
        lifecycleRegistry.markState(Lifecycle.State.CREATED)
    }

    abstract fun onItemSet()

    override fun getLifecycle(): Lifecycle = lifecycleRegistry
}
