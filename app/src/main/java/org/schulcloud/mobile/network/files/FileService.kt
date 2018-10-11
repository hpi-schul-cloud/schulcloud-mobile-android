package org.schulcloud.mobile.network.files

import okhttp3.ResponseBody
import org.jsoup.select.Evaluator
import org.schulcloud.mobile.models.file.DirectoryResponse
import org.schulcloud.mobile.models.file.File
import org.schulcloud.mobile.models.file.SignedUrlResponse
import retrofit2.Call
import retrofit2.Response
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.HashMap

object FileService {
    private var counter = AtomicInteger()
    private var workers: MutableList<BaseWorker> = mutableListOf()
    val TAG = FileService::class.java.simpleName
   // private var workers: MutableList<BaseWorker> = mutableListOf()

    suspend fun downloadFile(responseUrl: SignedUrlResponse,callback: ){
        var worker = DownloadFileWorker()
        workers.plus(worker)
        counter.incrementAndGet()

        var arguments: HashMap<String, Any> = hashMapOf()
        arguments.plus(Pair<String,Any>("responseUrl",responseUrl))

        var file = worker.execute(arguments)?.body()
        callback.run()
    }

    fun uploadFile(){

    }

    abstract class BaseWorker(){
        companion object {
            val JOB_NOT_STARTED = 0
            val JOB_WORKING = 1
            val JOB_SUCCES = 2
            val JOB_ERROR = 3
        }

        val mUUID: UUID = UUID.randomUUID()
        protected var mCall: Call<ResponseBody>? = null
        protected var _status = JOB_NOT_STARTED
        protected var _executing = false
        val status: Int
            get() = _status
        val isExecuting: Boolean
            get() = _executing

        abstract suspend fun execute(hashMap: HashMap<String,Any>): Response<ResponseBody>?

        protected fun cancel(){
            if(mCall != null)
                mCall?.cancel()
        }
    }
}
