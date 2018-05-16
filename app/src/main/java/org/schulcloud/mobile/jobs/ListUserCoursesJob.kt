package org.schulcloud.mobile.jobs

import android.util.Log
import io.realm.Realm
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.config.Config
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.network.ApiService
import ru.gildor.coroutines.retrofit.awaitResponse

class ListUserCoursesJob(callback: RequestJobCallback) : RequestJob(callback) {

    companion object {
        val TAG: String = ListUserCoursesJob::class.java.simpleName
    }

    override suspend fun onRun() {

        val response = ApiService.getInstance().listUserCourses().awaitResponse()
        if(response.isSuccessful) {

            if (BuildConfig.DEBUG) Log.i(TAG, "Courses received")

            // Do some kind of syncing

            // Save Courses
            val receivedCourses = response.body()!!.data!!
            //SchulCloudDatabase.instance!!.courseDao().addCourses(receivedCourses)

            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                for(course in receivedCourses) {
                    realm.copyToRealmOrUpdate(course)
                }
            }
            realm.close()

        } else {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error while fetching courses list")
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
        }
    }

}