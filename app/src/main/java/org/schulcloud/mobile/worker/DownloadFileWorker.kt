package org.schulcloud.mobile.worker

import android.app.Activity
import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseActivity
import org.schulcloud.mobile.models.file.File
import org.schulcloud.mobile.models.file.SignedUrlRequest
import org.schulcloud.mobile.network.ApiService
import org.schulcloud.mobile.utils.showGenericError
import retrofit2.HttpException
import ru.gildor.coroutines.retrofit.await

class DownloadFileWorker(private val file: File,private val download: Boolean, val context: Context): Worker() {
    val mContext = context
    val mActivity = context as BaseActivity
    val channelID = "PLACEHOLDER"

    override fun doWork(): Result {
        var notification = NotificationCompat.Builder(context,channelID)
                .setContentTitle(context.resources.getString(R.string.file_fileDownload_progress))
                .setProgress(100,0,true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        try {
            val response = ApiService.getInstance().generateSignedUrl(
                    SignedUrlRequest().apply {
                        action = SignedUrlRequest.ACTION_GET
                        path = file.key
                        fileType = file.type
                    })

            if(download) {

            }

        } catch (e: HttpException) {
            @Suppress("MagicNumber")
            when (e.code()) {
                404 -> context.showGenericError(R.string.file_fileOpen_error_404)
            }
        }
        return Result.SUCCESS
    }
}
