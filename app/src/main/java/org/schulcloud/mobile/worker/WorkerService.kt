package org.schulcloud.mobile.worker

import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import org.schulcloud.mobile.models.file.File
import org.schulcloud.mobile.worker.Models.DownloadFileWorker
import java.util.*

object WorkerService {
    private val workers: MutableList<WorkerInfo> = mutableListOf()
    private val workManager = WorkManager.getInstance()
    private val TAG = WorkerService::class.java.simpleName

    fun downloadFile(responseUrl: String, fileName: String, fileKey: String, params: Data): UUID? {
        workers.forEach {
            if(it.inputData.getString(DownloadFileWorker.KEY_FILEKEY) == fileKey){
                Log.i(TAG,"This File is already being downloaded!") //Placeholder
                return null
            }
        }

        val workRequest = OneTimeWorkRequest.Builder(DownloadFileWorker::class.java)
                .setInputData(params)
                .build()
        workers.plus(WorkerInfo(workRequest.id,params))
        workManager.enqueue(workRequest)

        return workRequest.id
    }

    fun uploadFile(responseUrl: String, file: File){

    }

    fun parseMap(params: Map<String,Unit>): Data{
        var Data = Data.Builder()
        params.forEach { key, value ->
            Data.put(key,value)
        }
        return Data.build()
    }

}
