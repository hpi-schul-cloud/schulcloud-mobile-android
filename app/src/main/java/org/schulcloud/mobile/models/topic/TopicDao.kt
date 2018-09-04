package org.schulcloud.mobile.models.topic

import androidx.lifecycle.LiveData
import io.realm.Realm
import org.schulcloud.mobile.utils.allAsLiveData
import org.schulcloud.mobile.utils.firstAsLiveData

class TopicDao(private val realm: Realm) {

    fun topicsForCourse(courseId: String): LiveData<List<Topic>> {
        return realm.where(Topic::class.java)
                .equalTo("courseId", courseId)
                .sort("position")
                .allAsLiveData()
    }

    fun topic(id: String): LiveData<Topic?> {
        return realm.where(Topic::class.java)
                .equalTo("id", id)
                .firstAsLiveData()
    }
}
