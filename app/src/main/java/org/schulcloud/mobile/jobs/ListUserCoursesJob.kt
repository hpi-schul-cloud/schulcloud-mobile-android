package org.schulcloud.mobile.jobs

import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.Sync
import org.schulcloud.mobile.models.course.Course
import org.schulcloud.mobile.network.ApiService
import org.schulcloud.mobile.utils.loge
import org.schulcloud.mobile.utils.logi
import ru.gildor.coroutines.retrofit.awaitResponse

class ListUserCoursesJob(callback: RequestJobCallback) : RequestJob(callback) {

    companion object {
        val TAG: String = ListUserCoursesJob::class.java.simpleName
    }

    override suspend fun onRun() {

        val response = ApiService.getInstance().listUserCourses().awaitResponse()
        if(response.isSuccessful) {

            logi(TAG, "Courses received")

            // Sync
            Sync.Data.with(Course::class.java, response.body()!!.data!!)
                    .run()

        } else {
            loge(TAG, "Error while fetching courses list")
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
        }
    }

}
