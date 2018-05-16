package org.schulcloud.mobile.models.course

import io.realm.Realm
import org.schulcloud.mobile.models.base.LiveRealmData
import org.schulcloud.mobile.utils.asLiveData

class CourseDao(private val realm: Realm) {

    fun listCourses(): LiveRealmData<Course> {
        return realm.where(Course::class.java)
                .findAllAsync()
                .asLiveData()
    }
}