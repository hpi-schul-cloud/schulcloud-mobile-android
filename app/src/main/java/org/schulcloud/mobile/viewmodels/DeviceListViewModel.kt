package org.schulcloud.mobile.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import io.realm.Realm
import io.realm.RealmResults
import org.schulcloud.mobile.models.notifications.Device
import org.schulcloud.mobile.models.notifications.NotificationRepository

class DeviceListViewModel: ViewModel(){

    private val realm by lazy{
        Realm.getDefaultInstance()
    }

    private var devices: LiveData<RealmResults<Device>?> = NotificationRepository.getDevices(realm)

    fun getDevices(): LiveData<RealmResults<Device>?>{
        return devices
    }

    fun resyncDevices(){
        devices = NotificationRepository.getDevices(realm)
    }
}