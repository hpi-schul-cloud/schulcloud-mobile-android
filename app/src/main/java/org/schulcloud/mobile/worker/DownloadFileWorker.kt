package org.schulcloud.mobile.worker

import android.Manifest
import android.app.Activity
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.experimental.runBlocking
import okhttp3.ResponseBody
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseActivity
import org.schulcloud.mobile.models.file.File
import org.schulcloud.mobile.models.file.SignedUrlRequest
import org.schulcloud.mobile.models.file.SignedUrlResponse
import org.schulcloud.mobile.network.ApiService
import org.schulcloud.mobile.utils.fileExtension
import org.schulcloud.mobile.utils.showGenericError
import org.schulcloud.mobile.utils.showGenericSuccess
import org.schulcloud.mobile.utils.writeToDisk
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response
import ru.gildor.coroutines.retrofit.await
import java.util.*

class DownloadFileWorker(): Worker() {
    companion object {
        val ERROR_SAVE_TO_DISK = 1
        val SUCCESS = 0
    }

    val channelID = "PLACEHOLDER"
    val responseUrl = inputData.getString("responseUrl")
    val fileName = inputData.getString("fileName")
    var output = Data.Builder()

    override fun doWork(): Result {
        try{
            var outcome: ResponseBody? = null
            runBlocking{outcome = ApiService.getInstance().downloadFile(responseUrl!!).await()}
            if(!outcome!!.writeToDisk(fileName!!)){
                outputData = output.putInt("result",1).build()
                return Result.FAILURE
            }
        } catch (e: HttpException) {
            @Suppress("MagicNumber", "UNREACHABLE_CODE")
            when (e.code()) {
                404 -> outputData = output.putInt("result",404).build()
            }
            return Result.FAILURE
        }
        outputData = output.putInt("result",0).build()
        return Result.SUCCESS
    }
}
