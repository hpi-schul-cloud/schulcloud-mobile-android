package org.schulcloud.mobile.controllers.learnstore

import android.view.LayoutInflater
import android.view.ViewGroup
import org.schulcloud.mobile.controllers.base.BaseAdapter
import org.schulcloud.mobile.controllers.base.BaseViewHolder
import org.schulcloud.mobile.databinding.ItemMaterialBinding
import org.schulcloud.mobile.models.material.Material

class MaterialListAdapter
    : BaseAdapter<Material, MaterialListAdapter.MaterialViewHolder, ItemMaterialBinding>() {

    fun update(materials: List<Material>) {
        items = materials
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        val binding = ItemMaterialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MaterialViewHolder(binding)
    }

    class MaterialViewHolder(binding: ItemMaterialBinding) : BaseViewHolder<Material, ItemMaterialBinding>(binding) {
        override fun onItemSet() {
            binding.material = item
            binding.viewHolder = this
        }
    }
}
