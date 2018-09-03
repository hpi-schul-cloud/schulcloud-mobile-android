package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm
import org.schulcloud.mobile.models.topic.Topic
import org.schulcloud.mobile.models.topic.TopicRepository

/**
 * Date: 6/9/2018
 */
class TopicViewModel(id: String) : ViewModel() {

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    private val _topic: LiveData<Topic?> = TopicRepository.topic(realm, id)

    val topic: LiveData<Topic?>
        get() = _topic
}
