package org.schulcloud.mobile.jobs

import android.util.Log
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.Sync
import org.schulcloud.mobile.models.topic.Topic
import org.schulcloud.mobile.network.ApiService
import ru.gildor.coroutines.retrofit.awaitResponse

class ListCourseTopicsJob(private val courseId: String, callback: RequestJobCallback) : RequestJob(callback) {
    companion object {
        val TAG: String = ListCourseTopicsJob::class.java.simpleName
    }

    override suspend fun onRun() {
        val response = ApiService.getInstance().listCourseTopics(courseId).awaitResponse()

        if (response.isSuccessful) {
            if (BuildConfig.DEBUG) Log.i(TAG, "Topics for course $courseId received")

            // Sync
            Sync.Data.with(Topic::class.java, response.body()!!.data!!).run()
        } else {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error while fetching topic list for course $courseId")
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
        }
    }
}
