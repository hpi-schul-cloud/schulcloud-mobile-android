package org.schulcloud.mobile.controllers.main

import android.view.LayoutInflater
import android.view.ViewGroup
import org.schulcloud.mobile.controllers.base.BaseAdapter
import org.schulcloud.mobile.controllers.base.BaseViewHolder
import org.schulcloud.mobile.controllers.base.OnItemSelectedCallback
import org.schulcloud.mobile.databinding.ItemDeviceBinding
import org.schulcloud.mobile.models.devices.Device

class DeviceListAdapter(private val selectedCallback: OnItemSelectedCallback):
        BaseAdapter<Device,DeviceListAdapter.DeviceViewHolder,ItemDeviceBinding>(){

    fun update(deviceList: List<Device>){
        items = deviceList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceListAdapter.DeviceViewHolder {
        val binding = ItemDeviceBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        binding.selectedCallback = selectedCallback
        return DeviceViewHolder(binding)
    }

    class DeviceViewHolder(binding: ItemDeviceBinding): BaseViewHolder<Device,ItemDeviceBinding>(binding){
        override fun onItemSet() {
            binding.device = item
        }
    }
}