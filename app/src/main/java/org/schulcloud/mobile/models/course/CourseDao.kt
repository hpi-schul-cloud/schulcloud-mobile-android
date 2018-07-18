package org.schulcloud.mobile.models.course

import io.realm.Realm
import org.schulcloud.mobile.models.base.LiveRealmData
import org.schulcloud.mobile.models.base.RealmObjectLiveData
import org.schulcloud.mobile.utils.asLiveData

class CourseDao(private val realm: Realm) {

    fun courses(): LiveRealmData<Course> {
        return realm.where(Course::class.java)
                .findAllAsync()
                .asLiveData()
    }
    fun course(id: String): RealmObjectLiveData<Course> {
        return realm.where(Course::class.java)
                .equalTo("id", id)
                .findFirstAsync()
                .asLiveData()
    }
}
