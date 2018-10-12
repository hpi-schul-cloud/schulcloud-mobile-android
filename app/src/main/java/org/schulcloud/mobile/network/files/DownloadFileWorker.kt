package org.schulcloud.mobile.network.files

import android.util.Log
import kotlinx.coroutines.experimental.async
import okhttp3.ResponseBody
import org.schulcloud.mobile.models.file.File
import org.schulcloud.mobile.models.file.SignedUrlResponse
import org.schulcloud.mobile.network.ApiService
import retrofit2.Call
import retrofit2.Response

class DownloadFileWorker(responseUrl: SignedUrlResponse): FileService.BaseWorker(){
    private var mResponseUrl = responseUrl
    val responseUrl: SignedUrlResponse
        get() = mResponseUrl

    override suspend fun execute(): Response<ResponseBody>? {
        _status = JOB_WORKING

        try {
            mCall = ApiService.getInstance().downloadFile(mResponseUrl.url!!)
            var file: Response<ResponseBody>? = null
            async { file = mCall?.execute() }.await()
            _status = JOB_SUCCES
            return file
        }catch (e: Exception){
            _status = JOB_ERROR
            Log.i(FileService.TAG,e.message!!)
        }
        return null
    }
}
