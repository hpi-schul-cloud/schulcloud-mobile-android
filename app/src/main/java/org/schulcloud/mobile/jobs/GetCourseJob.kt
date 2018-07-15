package org.schulcloud.mobile.jobs

import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.Sync
import org.schulcloud.mobile.models.course.Course
import org.schulcloud.mobile.network.ApiService
import org.schulcloud.mobile.utils.loge
import org.schulcloud.mobile.utils.logi
import ru.gildor.coroutines.retrofit.awaitResponse

class GetCourseJob(private val courseId: String, callback: RequestJobCallback) : RequestJob(callback) {
    companion object {
        val TAG: String = GetCourseJob::class.java.simpleName
    }

    override suspend fun onRun() {
        val response = ApiService.getInstance().getCourse(courseId).awaitResponse()

        if (response.isSuccessful) {
            logi(TAG, "Course $courseId received")

            // Sync
            Sync.SingleData.with(Course::class.java, response.body()!!).run()

        } else {
            loge(TAG, "Error while fetching course $courseId")
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
        }
    }
}
