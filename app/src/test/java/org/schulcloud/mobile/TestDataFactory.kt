package org.schulcloud.mobile

import androidx.lifecycle.LiveData
import org.schulcloud.mobile.models.course.Course
import org.schulcloud.mobile.utils.asLiveData

fun courseList(number: Int): List<Course> {
    val courses = mutableListOf<Course>()
    for (i in 1..number)
        courses.add(course(i.toString()))
    return courses
}

fun course(uniqueSequence : String): Course {
    return Course().apply {
        id = uniqueSequence
    }
}
