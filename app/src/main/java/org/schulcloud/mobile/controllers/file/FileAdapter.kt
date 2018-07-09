package org.schulcloud.mobile.controllers.file

import android.view.LayoutInflater
import android.view.ViewGroup
import org.schulcloud.mobile.controllers.base.BaseAdapter
import org.schulcloud.mobile.controllers.base.BaseViewHolder
import org.schulcloud.mobile.controllers.base.OnItemSelectedCallback
import org.schulcloud.mobile.databinding.ItemFileBinding
import org.schulcloud.mobile.models.file.File

class FileAdapter(private val selectedCallback: OnItemSelectedCallback)
    : BaseAdapter<File, FileAdapter.FileViewHolder, ItemFileBinding>() {

    fun update(fileList: List<File>) {
        items = fileList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val binding = ItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.selectedCallback = selectedCallback
        return FileViewHolder(binding)
    }

    class FileViewHolder(binding: ItemFileBinding) : BaseViewHolder<File, ItemFileBinding>(binding) {
        override fun onItemSet() {
            binding.file = item
        }
    }
}
