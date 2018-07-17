package org.schulcloud.mobile.controllers.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseAdapter
import org.schulcloud.mobile.controllers.base.BaseViewHolder
import org.schulcloud.mobile.controllers.base.OnItemSelectedCallback
import org.schulcloud.mobile.databinding.ItemDeviceBinding
import org.schulcloud.mobile.models.notifications.Device

class DeviceListAdapter(private val selectedCallback: OnItemSelectedCallback,private val mChevron: ImageView,private val mEmpty: TextView):
        BaseAdapter<Device,DeviceListAdapter.DeviceViewHolder,ItemDeviceBinding>(){

    private var active = false
    private val states = arrayOf(R.anim.rotate_0_180,R.anim.rotate_180_0)
    private lateinit var mRecyclerGroup: ViewGroup

    fun Boolean.toInt() = if (this) 1 else 0

    fun update(deviceList: List<Device>){
        if(active)
            items = deviceList
        else
            items = ArrayList<Device>()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        mRecyclerGroup = recyclerView.parent as ViewGroup
        super.onAttachedToRecyclerView(recyclerView)
    }

    fun changeState(deviceList: List<Device>){
        mChevron.startAnimation(AnimationUtils.loadAnimation(mChevron.context,states[active.toInt()]))
        active = !active
        if(deviceList.isEmpty() && active)
            mEmpty.visibility = View.VISIBLE
        else
            mEmpty.visibility = View.GONE
        update(deviceList)
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