package org.schulcloud.mobile.worker

import android.Manifest
import android.app.Activity
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import kotlinx.coroutines.experimental.runBlocking
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

class DownloadFileWorker(private val file: File,private val response: SignedUrlResponse, val context: Context): Worker() {
    val mActivity = context as BaseActivity
    val channelID = "PLACEHOLDER"
    var notificationManager = NotificationManagerCompat.from(context)

    override fun doWork(): Result {
        var notification = NotificationCompat.Builder(context,channelID)
                .setContentTitle(context.resources.getString(R.string.file_fileDownload_progress))
                .setProgress(100,0,true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        var notificationId = Random().nextInt(Int.MAX_VALUE)

        try{

            if(runBlocking{!mActivity.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)}){
                context.showGenericError(R.string.file_fileDownload_error_savePermissionDenied)
                return Result.FAILURE
            }

            //TODO: have a way to generate notificaiton ids, maybe make other way of making notifications
            notificationManager.notify(0,notification.build())
            var outcome = runBlocking{ApiService.getInstance().downloadFile(response.url!!).await()}
            if(!outcome.writeToDisk(file.name.orEmpty())){
                context.showGenericError(R.string.file_fileDownload_error_save)
                return Result.FAILURE
            }
            context.showGenericSuccess(R.string.file_fileDownload_success)
            notificationManager.cancel(notificationId)
        } catch (e: HttpException) {
            @Suppress("MagicNumber")
            when (e.code()) {
                404 -> context.showGenericError(R.string.file_fileOpen_error_404)
            }
        }
        return Result.SUCCESS
    }
}
