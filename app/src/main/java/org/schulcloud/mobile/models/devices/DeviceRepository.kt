package org.schulcloud.mobile.models.devices

import io.realm.Realm
import okhttp3.internal.http2.ErrorCode
import org.schulcloud.mobile.jobs.GetDeviceJob
import org.schulcloud.mobile.jobs.GetDevicesJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.base.LiveRealmData
import org.schulcloud.mobile.models.base.RealmObjectLiveData
import org.schulcloud.mobile.utils.devicesDao

object DeviceRepository{

    fun getDevices(realm: Realm): LiveRealmData<Device>{
        return realm.devicesDao().devices()
    }

    fun getDevice(deviceId: String, realm: Realm): RealmObjectLiveData<Device>{
        return realm.devicesDao().device(deviceId)
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