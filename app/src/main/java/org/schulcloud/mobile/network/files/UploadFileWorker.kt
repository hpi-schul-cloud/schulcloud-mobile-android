package org.schulcloud.mobile.network.files

import android.util.Log
import kotlinx.coroutines.experimental.async
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.schulcloud.mobile.models.file.SignedUrlResponse
import org.schulcloud.mobile.network.ApiService
import retrofit2.Response

class UploadFileWorker(responseUrl: SignedUrlResponse, requestBody: RequestBody): FileService.BaseWorker() {
    override val type = "upload"
    val mResponseUrl = responseUrl
    val mRequestBody = requestBody

    override suspend fun execute(): Response<ResponseBody>? {
        _status = JOB_WORKING
        mCall = ApiService.getInstance().uploadFile(
                mResponseUrl.url!!,
                mResponseUrl.header?.contentType!!,
                mResponseUrl.header?.metaPath!!,
                mResponseUrl.header?.metaName!!,
                mResponseUrl.header?.metaFlatName!!,
                mResponseUrl.header?.metaThumbnail!!,
                mRequestBody
        )
        var response: Response<ResponseBody>?

        try {
            async { response = mCall?.execute() }.await()
            _status = JOB_SUCCESS
        }catch(e: Exception){
            _status = JOB_ERROR
            Log.i(FileService.TAG,"Error while uploading file")
        }

        return null
    }

    override fun cancel(){
        super.cancel()
        FileService.workersUpload.remove(this)
    }
}
