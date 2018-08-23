package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.models.topic.Topic
import org.schulcloud.mobile.models.topic.TopicRepository

class TopicViewModel(val id: String) : ViewModel() {
    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    val topic: LiveData<Topic?> = TopicRepository.topic(realm, id)

    fun course(id: String) = CourseRepository.course(realm, id)
}
