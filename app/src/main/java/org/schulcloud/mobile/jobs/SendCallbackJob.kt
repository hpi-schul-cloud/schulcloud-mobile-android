package org.schulcloud.mobile.jobs

import android.util.Log
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.notifications.CallbackRequest
import org.schulcloud.mobile.network.ApiService
import ru.gildor.coroutines.retrofit.awaitResponse

class SendCallbackJob(private val callbackRequest: CallbackRequest,requestJobCallback: RequestJobCallback): RequestJob(requestJobCallback) {
    companion object {
        private val TAG = SendCallbackJob::class.java.simpleName
    }

    override suspend fun onRun() {
        val response = ApiService.getInstance().sendCallback(callbackRequest).awaitResponse()

        if(response.isSuccessful){
            if (BuildConfig.DEBUG) Log.i(TAG,"Successfully sent callback ${callbackRequest.notificationId}")

            callback?.success()
        }else{
            if(BuildConfig.DEBUG) Log.i(TAG,"Unable to send callback")

            callback?.error(RequestJobCallback.ErrorCode.ERROR)
        }
    }
}