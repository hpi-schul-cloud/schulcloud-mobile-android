package org.schulcloud.mobile.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import io.realm.Realm
import org.schulcloud.mobile.models.devices.Device
import org.schulcloud.mobile.models.devices.DeviceRepository

class DeviceViewModel(id: String): ViewModel(){

    private val realm by lazy{
        Realm.getDefaultInstance()
    }

    private val device: LiveData<Device?> = DeviceRepository.getDevice(id,realm)

    fun getDevice(): LiveData<Device?>{
        return device
    }
}