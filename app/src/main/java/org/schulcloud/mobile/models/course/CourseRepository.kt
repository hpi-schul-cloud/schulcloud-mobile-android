package org.schulcloud.mobile.models.course

import androidx.lifecycle.LiveData
import io.realm.Realm
import org.schulcloud.mobile.jobs.GetCourseJob
import org.schulcloud.mobile.jobs.ListUserCoursesJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.utils.courseDao

object CourseRepository {
    fun courses(realm: Realm): LiveData<List<Course>> {
        return realm.courseDao().courses()
    }

    fun course(realm: Realm, id: String): LiveData<Course?> {
        return realm.courseDao().course(id)
    }

    suspend fun syncCourses() {
        ListUserCoursesJob(RequestJobCallback()).run()
    }

    suspend fun syncCourse(courseId: String) {
        GetCourseJob(courseId, RequestJobCallback()).run()
    }
}
