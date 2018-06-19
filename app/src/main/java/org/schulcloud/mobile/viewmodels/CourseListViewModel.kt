package org.schulcloud.mobile.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import io.realm.Realm
import io.realm.RealmResults
import org.schulcloud.mobile.models.course.Course
import org.schulcloud.mobile.models.course.CourseRepository

class CourseListViewModel : ViewModel() {

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    private var courses: LiveData<RealmResults<Course>?> = CourseRepository.listCourses(realm)

    fun getCourses(): LiveData<RealmResults<Course>?> {
        return courses
    }
}
