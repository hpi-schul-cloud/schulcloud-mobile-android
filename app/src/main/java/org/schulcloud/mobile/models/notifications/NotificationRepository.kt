package org.schulcloud.mobile.models.notifications

import com.google.gson.JsonParser
import io.realm.Realm
import org.schulcloud.mobile.jobs.*
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.base.LiveRealmData
import org.schulcloud.mobile.models.base.RealmObjectLiveData
import org.schulcloud.mobile.storages.DeviceStorage
import org.schulcloud.mobile.utils.devicesDao

object NotificationRepository{

    @JvmStatic
    var deviceToken: String? = null
        get() = DeviceStorage().deviceToken

    fun getDevices(realm: Realm): LiveRealmData<Device>{
        return realm.devicesDao().devices()
    }

    fun getDevice(deviceId: String, realm: Realm): RealmObjectLiveData<Device>{
        return realm.devicesDao().device(deviceId)
    }

    fun createDevice(device: DeviceRequest, callback: RequestJobCallback) {
        CreateDeviceJob(device,callback)
    }

    fun deleteDevice(id: String, callback: RequestJobCallback) {
        DeleteDeviceJob(id, callback)
    }

    fun sendCallback(callbackRequest: CallbackRequest,callback: RequestJobCallback){
    }

    suspend fun syncDevices() {
        GetDevicesJob( object : RequestJobCallback() {
            override fun onSuccess() {
            }

            override fun onError(code: ErrorCode) {
            }
        }).run()
    }

    suspend fun syncDevice(deviceId: String) {
        GetDeviceJob(deviceId, object : RequestJobCallback() {
            override fun onSuccess() {
            }

            override fun onError(code: ErrorCode) {
            }
        }).run()
    }
}