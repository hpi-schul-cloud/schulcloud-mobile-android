package org.schulcloud.mobile.controllers.topic

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.schulcloud.mobile.controllers.base.BaseViewHolder
import org.schulcloud.mobile.databinding.ItemResourceBinding
import org.schulcloud.mobile.models.content.Resource
import org.schulcloud.mobile.utils.openUrl
import org.schulcloud.mobile.utils.resolveRedirect

/**
 * Date: 6/10/2018
 */
class ResourceListAdapter : RecyclerView.Adapter<ResourceListAdapter.ResourceViewHolder>() {
    private var resources: List<Resource> = emptyList()

    fun update(resources: List<Resource>) {
        this.resources = resources
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResourceViewHolder {
        val binding = ItemResourceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResourceListAdapter.ResourceViewHolder(binding)
    }

    override fun getItemCount(): Int = resources.size

    override fun onBindViewHolder(holder: ResourceViewHolder, position: Int) {
        holder.item = resources[position]
    }

    class ResourceViewHolder(binding: ItemResourceBinding) : BaseViewHolder<Resource, ItemResourceBinding>(binding) {
        override fun onItemSet() {
            binding.resource = item
            binding.viewHolder = this
        }

        fun openExternal() {
            async(UI) {
                resolveRedirect(item.url!!)?.also { openUrl(this@ResourceViewHolder.context, it) }
            }
        }
    }
}
