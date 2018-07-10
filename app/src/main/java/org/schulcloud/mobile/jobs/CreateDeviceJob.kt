package org.schulcloud.mobile.jobs

import android.os.Debug
import android.util.Log
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.devices.Device
import org.schulcloud.mobile.models.devices.DeviceRepository
import org.schulcloud.mobile.network.ApiService
import ru.gildor.coroutines.retrofit.awaitResponse

class CreateDeviceJob(private val device: Device, callback:RequestJobCallback): RequestJob(callback){

    companion object {
        private val TAG: String = CreateDeviceJob::class.java.simpleName
    }

    override suspend fun onRun() {
        val response = ApiService.getInstance().createDevice(device).awaitResponse()

        if(response.isSuccessful){
            if (BuildConfig.DEBUG) Log.i(TAG,"sucessfully created device ${response.body()}")

            DeviceRepository.syncDevices()

            callback?.success()
        }else{
            if(BuildConfig.DEBUG) Log.i(TAG,"unable to create device")

            callback?.error(RequestJobCallback.ErrorCode.ERROR)
        }
    }

}