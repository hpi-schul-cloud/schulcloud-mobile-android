package org.schulcloud.mobile.network.files

import kotlinx.coroutines.experimental.async
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.schulcloud.mobile.models.file.SignedUrlResponse
import retrofit2.Call
import retrofit2.Response
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.HashMap

object FileService {
    private var counterDownload = AtomicInteger()
    val workersDownload: MutableList<DownloadFileWorker> = mutableListOf()
    private var counterUpload = AtomicInteger()
    val workersUpload: MutableList<UploadFileWorker> = mutableListOf()
    val TAG = FileService::class.java.simpleName

    suspend fun downloadFile(responseUrl: SignedUrlResponse,callback: (responseBody: ResponseBody?) -> Unit){
        if(isBeingDownloaded(responseUrl))
            return

        var worker = DownloadFileWorker(responseUrl)
        workersDownload.add(worker)
        counterDownload.incrementAndGet()

        var arguments: HashMap<String, Any> = hashMapOf()
        arguments.plus(Pair<String,Any>("responseUrl",responseUrl))

        var file = worker.execute()?.body()
        workersDownload.remove(worker)
        callback(file)
    }

    suspend fun uploadFile(file: java.io.File,responseUrl: SignedUrlResponse, callback: (success: Boolean) -> Unit){

        val requestBody: RequestBody = RequestBody.create(MediaType.parse("file/*"),file)
        val worker = UploadFileWorker(responseUrl,requestBody)
        var response: Response<ResponseBody>? = null

        counterUpload.incrementAndGet()
        workersUpload.add(worker)

        async { response = worker.execute() }.await()

        counterUpload.decrementAndGet()
        workersUpload.remove(worker)

        if(response!!.isSuccessful)
            callback(true)
        else
            callback(false)

    }

    fun isBeingDownloaded(responseUrl: SignedUrlResponse): Boolean{
        workersDownload.forEach {
            if(it.responseUrl.header?.metaPath + it.responseUrl.header?.metaName == responseUrl.header?.metaPath + responseUrl.header?.metaName)
                return true
        }
        return false
    }


    fun getDownloadWorker(uuid: UUID): DownloadFileWorker?{
        workersDownload.forEach {
            if(it.mUUID == uuid)
                return it
        }
        return null
    }

    abstract class BaseWorker(){
        companion object {
            val JOB_NOT_STARTED = 0
            val JOB_WORKING = 1
            val JOB_SUCCESS = 2
            val JOB_ERROR = 3
        }

        val mUUID: UUID = UUID.randomUUID()
        protected var mCall: Call<ResponseBody>? = null
        protected var _status = JOB_NOT_STARTED
        protected var _executing = false
        open val type = "base"
        val status: Int
            get() = _status
        val isExecuting: Boolean
            get() = _executing

        abstract suspend fun execute(): Response<ResponseBody>?

        protected open fun cancel(){
            if(mCall != null)
                mCall?.cancel()
        }
    }
}
