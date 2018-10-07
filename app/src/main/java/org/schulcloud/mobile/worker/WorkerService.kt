package org.schulcloud.mobile.worker

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseActivity
import org.schulcloud.mobile.models.file.File
import org.schulcloud.mobile.utils.NotificationUtils
import org.schulcloud.mobile.worker.models.DownloadFileWorker
import java.util.*

object WorkerService {
    private val workers: MutableList<WorkerInfo> = mutableListOf()
    private val workManager = WorkManager.getInstance()
    private val TAG = WorkerService::class.java.simpleName

    fun downloadFile(responseUrl: String, file: File, context: Context, params: Data): UUID? {
        workers.forEach {
            if(it.inputData.getString(DownloadFileWorker.KEY_FILEKEY) == file.key){
                Log.i(TAG,"This File is already being downloaded!") //Placeholder
                return null
            }
        }


        val workRequest = OneTimeWorkRequest.Builder(DownloadFileWorker::class.java)
                .setInputData(params)
                .build()
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

    fun addWorker(worker: WorkerInfo){
        workers.plus(worker)
    }

    fun workerFinished(uuid: UUID){
        workers.forEach {
            if(it.id == uuid)
                workers.remove(it)
        }
    }

    class WorkerInfo(id: UUID,inputData: Data) {
        val id: UUID = id
        val inputData: Data = inputData
    }
}
