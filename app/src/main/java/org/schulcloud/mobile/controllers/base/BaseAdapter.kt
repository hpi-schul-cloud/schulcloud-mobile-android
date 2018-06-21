package org.schulcloud.mobile.controllers.base

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import kotlin.properties.Delegates

/**
 * Date: 6/21/2018
 */
abstract class BaseAdapter<T : Any, VH : BaseViewHolder<T, B>, B : ViewDataBinding>
    : RecyclerView.Adapter<VH>() {
    var items: List<T> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.item = items[position]
    }
}
