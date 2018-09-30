package org.schulcloud.mobile.worker

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import org.schulcloud.mobile.R
import org.schulcloud.mobile.models.file.File
import org.schulcloud.mobile.utils.NotificationUtils
import org.schulcloud.mobile.worker.models.DownloadFileWorker
import java.util.*

object WorkerService {
    private val workers: MutableList<WorkerInfo> = mutableListOf()
    private val workManager = WorkManager.getInstance()
    private val TAG = WorkerService::class.java.simpleName

    fun downloadFile(responseUrl: String, file: File, context: Context, params: Data): UUID? {
        val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java)
        var notification = NotificationCompat.Builder(context, NotificationUtils.channelId)
                .setContentTitle(context.resources.getString(R.string.file_fileDownload_progress))
                .setProgress(100,0,true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.mipmap.ic_launcher,0)
                .build()
        var notificationId = Random().nextInt(Int.MAX_VALUE)

        workers.forEach {
            if(it.inputData.getString(DownloadFileWorker.KEY_FILEKEY) == file.key){
                Log.i(TAG,"This File is already being downloaded!") //Placeholder
                return null
            }
        }

        notificationManager!!.notify(notificationId,notification)

        val workRequest = OneTimeWorkRequest.Builder(DownloadFileWorker::class.java)
                .setInputData(params)
                .build()
        workers.plus(WorkerInfo(workRequest.id,params))
        workManager.enqueue(workRequest)

        notificationManager.cancel(notificationId)

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
