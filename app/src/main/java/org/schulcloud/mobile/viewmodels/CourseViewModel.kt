package org.schulcloud.mobile.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import io.realm.Realm
import org.schulcloud.mobile.models.course.Course
import org.schulcloud.mobile.models.course.CourseRepository

/**
 * Date: 6/9/2018
 */
class CourseViewModel(private val id: String) : ViewModel() {

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    private val _course: LiveData<Course> = CourseRepository.course(realm, id)

    val course: LiveData<Course>
        get() = _course
}
