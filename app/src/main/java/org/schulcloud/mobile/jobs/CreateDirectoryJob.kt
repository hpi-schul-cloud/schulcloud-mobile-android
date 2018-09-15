package org.schulcloud.mobile.jobs

import android.util.Log
import com.google.gson.Gson
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.file.DirectoryRequest
import org.schulcloud.mobile.network.ApiService
import ru.gildor.coroutines.retrofit.awaitResponse

class CreateDirectoryJob(private val directoryRequest: DirectoryRequest, callback: RequestJobCallback): RequestJob(callback) {
    companion object {
        val TAG: String = CreateDirectoryJob::class.java.simpleName
    }

    override suspend fun onRun() {
        var gson = Gson()
        if (BuildConfig.DEBUG) Log.i(TAG,gson.toJson(directoryRequest))
        val response = ApiService.getInstance().createDirectory(directoryRequest).awaitResponse()

        if (response.isSuccessful) {
            if (BuildConfig.DEBUG) Log.i(TAG, "Directory ${directoryRequest.path} created!")
            callback?.success()
        } else {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error while creating directory ${directoryRequest.path}!")
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
        }
    }
}
