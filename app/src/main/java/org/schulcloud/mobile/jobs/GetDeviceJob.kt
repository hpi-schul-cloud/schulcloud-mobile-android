package org.schulcloud.mobile.jobs

import android.util.Log
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.Sync
import org.schulcloud.mobile.models.devices.Device
import org.schulcloud.mobile.network.ApiService
import ru.gildor.coroutines.retrofit.awaitResponse


class GetDeviceJob(private val deviceId: String, callback: RequestJobCallback): RequestJob(callback){
    companion object {
        val TAG: String = GetDeviceJob::class.java.simpleName
    }

    override suspend fun onRun() {
        val response = ApiService.getInstance().getDevice(deviceId).awaitResponse()

        if (response.isSuccessful) {
            if (BuildConfig.DEBUG) Log.i(TAG, "Device $deviceId received")

            // Sync
            Sync.SingleData.with(Device::class.java, response.body()!!).run()

        } else {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error while fetching device $deviceId")
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
        }
    }
}