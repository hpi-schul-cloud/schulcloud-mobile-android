package org.schulcloud.mobile.jobs

import android.util.Log
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.devices.DeviceRepository
import org.schulcloud.mobile.network.ApiService
import ru.gildor.coroutines.retrofit.awaitResponse

class DeleteDeviceJob(private val deviceId: String, callback: RequestJobCallback): RequestJob(callback){
    companion object {
        private val TAG = DeleteDeviceJob::class.java.simpleName
    }

    override suspend fun onRun() {
        var response = ApiService.getInstance().deleteDevice(deviceId).awaitResponse()

        if(response.isSuccessful){
            if(BuildConfig.DEBUG) Log.i(TAG,"Successfully deleted device $deviceId")

            DeviceRepository.syncDevices()

            callback?.success()
        }else{
            if(BuildConfig.DEBUG) Log.i(TAG,"Unable to delete device $deviceId")

            callback?.error(RequestJobCallback.ErrorCode.ERROR)
        }
    }

}