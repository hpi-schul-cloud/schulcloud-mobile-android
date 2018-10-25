package org.schulcloud.mobile.controllers.learnstore

import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import org.schulcloud.mobile.controllers.base.BaseAdapter
import org.schulcloud.mobile.controllers.base.BaseViewHolder
import org.schulcloud.mobile.databinding.ItemMaterialBinding
import org.schulcloud.mobile.models.material.Material
import org.schulcloud.mobile.views.NestableFlexboxLayoutManager

class MaterialListAdapter
    : BaseAdapter<Material, MaterialListAdapter.MaterialViewHolder, ItemMaterialBinding>() {

    private val tagAdapter: TagAdapter by lazy {
        TagAdapter()
    }

    fun update(materials: List<Material>) {
        items = materials
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        val binding = ItemMaterialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.recyclerViewTags.apply {
            layoutManager = NestableFlexboxLayoutManager(this.context).apply {
                flexWrap = FlexWrap.WRAP
                flexDirection = FlexDirection.ROW
                alignItems = AlignItems.STRETCH
            }
            adapter = tagAdapter
        }
        return MaterialViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {

    }

    class MaterialViewHolder(binding: ItemMaterialBinding) : BaseViewHolder<Material, ItemMaterialBinding>(binding) {
        companion object {
            @JvmStatic
            fun tagListToString(tagList: List<String>?): String? {
                return tagList?.joinToString(", ")
            }
        }

        override fun onItemSet() {
            binding.material = item
        }
    }
}
