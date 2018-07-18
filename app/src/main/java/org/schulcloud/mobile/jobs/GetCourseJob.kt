package org.schulcloud.mobile.jobs

import android.util.Log
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.Sync
import org.schulcloud.mobile.models.course.Course
import org.schulcloud.mobile.network.ApiService
import ru.gildor.coroutines.retrofit.awaitResponse

class GetCourseJob(private val courseId: String, callback: RequestJobCallback) : RequestJob(callback) {
    companion object {
        val TAG: String = GetCourseJob::class.java.simpleName
    }

    override suspend fun onRun() {
        val response = ApiService.getInstance().getCourse(courseId).awaitResponse()

        if (response.isSuccessful) {
            if (BuildConfig.DEBUG) Log.i(TAG, "Course $courseId received")

            // Sync
            Sync.SingleData.with(Course::class.java, response.body()!!).run()

        } else {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error while fetching course $courseId")
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
        }
    }
}
