package org.schulcloud.mobile.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import io.realm.Realm
import io.realm.RealmResults
import org.schulcloud.mobile.models.devices.Device
import org.schulcloud.mobile.models.devices.DeviceRepository

class DeviceListViewModel: ViewModel(){

    private val realm by lazy{
        Realm.getDefaultInstance()
    }

    private var devices: LiveData<RealmResults<Device>?> = DeviceRepository.getDevices(realm)

    fun getDevices(): LiveData<RealmResults<Device>?>{
        return devices
    }
}