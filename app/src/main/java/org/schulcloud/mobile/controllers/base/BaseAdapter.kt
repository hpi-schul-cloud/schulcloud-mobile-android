package org.schulcloud.mobile.controllers.base

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import org.schulcloud.mobile.utils.asVisibility
import kotlin.properties.Delegates

abstract class BaseAdapter<T : Any, VH : BaseViewHolder<T, B>, out B : ViewDataBinding>(
    var emptyIndicator: View? = null
) : RecyclerView.Adapter<VH>() {
    var items: List<T> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
        emptyIndicator?.visibility = items.isEmpty().asVisibility()
        recyclerView?.visibility = items.isNotEmpty().asVisibility()
    }

    var recyclerView: RecyclerView? = null
        private set

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = null
        super.onDetachedFromRecyclerView(recyclerView)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.item = items[position]
    }
}
