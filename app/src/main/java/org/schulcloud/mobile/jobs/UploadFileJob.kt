package org.schulcloud.mobile.jobs

import android.util.Log
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.network.ApiService
import ru.gildor.coroutines.retrofit.awaitResponse

class UploadFileJob(callback: RequestJobCallback): RequestJob(callback) {
    companion object {
        val TAG: String = UploadFileJob::class.java.simpleName
    }

    override suspend fun onRun() {
        val response = ApiService.getInstance().uploadFile().awaitResponse()

        if(response.isSuccessful){
            if(BuildConfig.DEBUG) Log.i(TAG,"Uploaded File ")
        }else{
            if(BuildConfig.DEBUG) Log.i(TAG,"Failed to upload File ")
        }
    }
}
