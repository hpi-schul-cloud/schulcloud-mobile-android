package org.schulcloud.mobile.models.course

import io.realm.Realm
import org.schulcloud.mobile.jobs.ListUserCoursesJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.base.LiveRealmData
import org.schulcloud.mobile.models.base.RealmObjectLiveData
import org.schulcloud.mobile.utils.courseDao

object CourseRepository {

    init {
        requestCourseList()
    }

    fun listCourses(realm: Realm): LiveRealmData<Course> {
        return realm.courseDao().listCourses()
    }

    fun course(realm: Realm, id: String): RealmObjectLiveData<Course> {
        return realm.courseDao().course(id)
    }

    //
    private fun requestCourseList() {
        ListUserCoursesJob(object : RequestJobCallback() {
            override fun onSuccess() {
            }

            override fun onError(code: ErrorCode) {
            }
        }).run()
    }
}
