package org.schulcloud.mobile.worker.models

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.experimental.runBlocking
import okhttp3.ResponseBody
import org.schulcloud.mobile.network.ApiService
import org.schulcloud.mobile.utils.writeToDisk
import ru.gildor.coroutines.retrofit.await

class DownloadFileWorker(context: Context, params: WorkerParameters): Worker(context,params) {
    companion object {
        val ERROR_SAVE_TO_DISK = 1
        val SUCCESS = 0
        val KEY_FILENAME = "fileName"
        val KEY_URL = "responseUrl"
        val KEY_FILEKEY = "fileKey"
    }

    val channelID = "PLACEHOLDER"
    val responseUrl = inputData.getString(KEY_URL)
    val fileName = inputData.getString(KEY_FILENAME)
    var output = Data.Builder()

    override fun doWork(): Result {
        var outcome: ResponseBody? = null
        runBlocking{outcome = ApiService.getInstance().downloadFile(responseUrl!!).await()}
        if(!outcome!!.writeToDisk(fileName!!)){
            outputData = output.putInt("result",1).build()
            return Result.FAILURE
        }
        outputData = output.putInt("result",0).build()
        return Result.SUCCESS
    }
}
