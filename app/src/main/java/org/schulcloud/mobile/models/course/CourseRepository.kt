package org.schulcloud.mobile.models.course

import io.realm.Realm
import org.schulcloud.mobile.jobs.ListUserCoursesJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.base.LiveRealmData
import org.schulcloud.mobile.utils.courseDao

object CourseRepository {

    init {
        requestCourseList()
    }

    fun listCourses(realm: Realm): LiveRealmData<Course> {
        return realm.courseDao().listCourses()
    }

    //
    private fun requestCourseList() {
        ListUserCoursesJob(object: RequestJobCallback() {
            override fun onSuccess() {
            }

            override fun onError(code: ErrorCode) {
            }
        }).run()
    }
}