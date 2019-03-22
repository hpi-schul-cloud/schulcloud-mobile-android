package org.schulcloud.mobile.models.course

import androidx.lifecycle.LiveData
import io.realm.Realm
import org.schulcloud.mobile.utils.allAsLiveData
import org.schulcloud.mobile.utils.firstAsLiveData

class CourseDao(private val realm: Realm) {
    fun courses(): LiveData<List<Course>> {
        return realm.where(Course::class.java)
                .allAsLiveData()
    }
    fun course(id: String): LiveData<Course?> {
        return realm.where(Course::class.java)
                .equalTo("id", id)
                .firstAsLiveData()
    }
}
