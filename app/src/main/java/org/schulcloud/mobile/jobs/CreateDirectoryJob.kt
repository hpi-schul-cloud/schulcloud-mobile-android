package org.schulcloud.mobile.jobs

import android.util.Log
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.network.ApiService
import ru.gildor.coroutines.retrofit.awaitResponse

class CreateDirectoryJob(private val path: String,callback: RequestJobCallback): RequestJob(callback) {
    companion object {
        val TAG: String = CreateDirectoryJob::class.java.simpleName
    }

    override suspend fun onRun() {
        val response = ApiService.getInstance().createDirectory(path).awaitResponse()

        if (response.isSuccessful) {
            if (BuildConfig.DEBUG) Log.i(TAG, "Directory $path created!")
            callback?.success()
        } else {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error while creating directory $path!")
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
        }
    }
}
