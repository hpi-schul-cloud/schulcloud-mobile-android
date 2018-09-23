package org.schulcloud.mobile.jobs

import android.util.Log
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.file.Directory
import org.schulcloud.mobile.network.ApiService
import ru.gildor.coroutines.retrofit.awaitResponse

class CreateDirectoryJob(private val directory: Directory, callback: RequestJobCallback? = null) : RequestJob(callback) {
    companion object {
        val TAG: String = CreateDirectoryJob::class.java.simpleName
    }

    override suspend fun onRun() {
        val response = ApiService.getInstance().createDirectory(directory).awaitResponse()

        if (response.isSuccessful) {
            if (BuildConfig.DEBUG) Log.i(TAG, "Directory at path ${directory.path} created")
            callback?.success()
        } else {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error while creating directory at path ${directory.path}")
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
        }
    }
}
