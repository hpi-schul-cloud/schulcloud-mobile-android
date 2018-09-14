package org.schulcloud.mobile.jobs

import android.util.Log
import okhttp3.MediaType
import okhttp3.RequestBody
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.file.File
import org.schulcloud.mobile.models.file.SignedUrlResponse
import org.schulcloud.mobile.network.ApiService
import ru.gildor.coroutines.retrofit.awaitResponse

class UploadFileJob(private val file: java.io.File,private val signedUrl: SignedUrlResponse,callback: RequestJobCallback) : RequestJob(callback) {
    companion object{
        val Tag: String = UploadFileJob::class.java.simpleName
    }

    override suspend fun onRun() {
        var request = RequestBody.create(MediaType.parse("file/*"),file)
        val response = ApiService.getInstance().uploadFile(signedUrl.url!!,
                signedUrl.header?.contentType!!,
                signedUrl.header?.metaPath!!,
                signedUrl.header?.metaName!!,
                signedUrl.header?.metaFlatName!!,
                signedUrl.header?.metaThumbnail!!,
                request).awaitResponse()

        if (response.isSuccessful) {
            if (BuildConfig.DEBUG) Log.i(CreateDirectoryJob.TAG, "File ${file.name} uploaded!")
            callback?.success()
        } else {
            if (BuildConfig.DEBUG) Log.e(CreateDirectoryJob.TAG, "Error while uploading file ${file.name}")
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
        }
    }
}
