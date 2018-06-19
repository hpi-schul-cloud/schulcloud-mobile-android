package org.schulcloud.mobile.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import io.realm.Realm
import io.realm.RealmResults
import org.schulcloud.mobile.models.course.Course
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.models.topic.Topic
import org.schulcloud.mobile.models.topic.TopicRepository

/**
 * Date: 6/9/2018
 */
class CourseViewModel(id: String) : ViewModel() {

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    private val _course: LiveData<Course?> = CourseRepository.course(realm, id)
    private val _topics: LiveData<RealmResults<Topic>?> = TopicRepository.listTopics(realm, id)

    val course: LiveData<Course?>
        get() = _course

    val topics: LiveData<RealmResults<Topic>?>
        get() = _topics
}
