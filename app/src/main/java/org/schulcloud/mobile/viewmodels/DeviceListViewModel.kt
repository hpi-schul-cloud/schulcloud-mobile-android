package org.schulcloud.mobile.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import io.realm.Realm
import kotlinx.coroutines.experimental.async
import org.schulcloud.mobile.controllers.settings.SettingsFragment
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.notifications.Device
import org.schulcloud.mobile.models.notifications.DeviceRequest
import org.schulcloud.mobile.models.notifications.NotificationRepository
import org.schulcloud.mobile.models.user.UserRepository

class DeviceListViewModel: ViewModel(){

    private val realm by lazy{
        Realm.getDefaultInstance()
    }

    private var devices: LiveData<List<Device>> = NotificationRepository.getDevices(realm)

    fun getDevices(): LiveData<List<Device>>{
        return devices
    }

    fun resyncDevices(){
        devices = NotificationRepository.getDevices(realm)
    }

    class deviceCallback(var viewModel: DeviceListViewModel): RequestJobCallback() {
        override fun onError(code: ErrorCode) {
            Log.i(SettingsFragment.TAG, ErrorCode.ERROR.toString())
        }

        override fun onSuccess() {
            async {NotificationRepository.syncDevices();viewModel. resyncDevices();}
        }
    }

    fun createDevice(){
        if(NotificationRepository.deviceToken == null) {
            NotificationRepository.deviceToken = FirebaseInstanceId.getInstance().token
        }

        NotificationRepository.createDevice(DeviceRequest("firebase","mobile",
                android.os.Build.MODEL + " ( " + android.os.Build.PRODUCT + " ) ",
                UserRepository.token!!,NotificationRepository.deviceToken!!,android.os.Build.VERSION.INCREMENTAL), deviceCallback(this))
    }

    fun deleteDevice(deviceId: String){
        NotificationRepository.deleteDevice(deviceId,deviceCallback(this))
    }
}