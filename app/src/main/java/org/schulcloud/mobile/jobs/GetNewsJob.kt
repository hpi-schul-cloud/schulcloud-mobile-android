package org.schulcloud.mobile.jobs

import android.util.Log
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.Sync
import org.schulcloud.mobile.models.news.News
import org.schulcloud.mobile.network.ApiService
import ru.gildor.coroutines.retrofit.awaitResponse

class GetNewsJob(private val newsId: String, callback: RequestJobCallback) : RequestJob(callback) {
    companion object {
        val TAG: String = GetCourseJob::class.java.simpleName
    }

    override suspend fun onRun() {
        val response = ApiService.getInstance().getNews(newsId).awaitResponse()

        if (response.isSuccessful) {
            if (BuildConfig.DEBUG) Log.i(TAG, "News $newsId received")

            // Sync
            Sync.SingleData.with(News::class.java, response.body()!!).run()
        } else {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error while fetching homework $newsId")
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
        }
    }
}
